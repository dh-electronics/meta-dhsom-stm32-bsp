#!/bin/bash
set -e

bitbake_target="${1}"
arch="${2:-arm}"

if [[ "$3" != "" ]]; then
    defconfig_name="$3"
elif [[ "$bitbake_target" == virtual/kernel ]]; then
    defconfig_name="arch/$arch/configs/dh_defconfig"
elif [[ "$bitbake_target" == virtual/bootloader ]]; then
    defconfig_name="configs/dh_defconfig"
else
    echo "$0: can't guess defconfig name." >&2
    exit 1
fi

# 1. Run the build up to the do_configure task after which sources and
#    configuration are ready and can be extracted from the workdir.
kas build .ci/kas-ci.yml --target "$bitbake_target" -c configure

# 2. Extract paths to the source and build directories and inject them into our
#    environment.
source <(kas shell .ci/kas-ci.yml -c 'bitbake -e '"$bitbake_target"' | grep -E "^[SB]="')
echo "Source Directory:    $S"
echo "Build Directory:     $B"

# 3. Create a staging clone of the sources.  This will be needed to inject a
#    defconfig and tag.
sourcedir="$(mktemp -d "sources-${bitbake_target/\//-}-XXXX")"
echo "Staging Sources:     $sourcedir"
git clone -o local "$S" "$sourcedir"

# 4. Detach from whatever branch was used - we will only reference by tag
#    later.
git -C "$sourcedir" switch --detach

# 5. Copy the configuration into the sources and use the kernel build-system to
#    make a defconfig from it.
cp -vt "$sourcedir" "$B/.config"
(cd "$sourcedir"; ARCH="$arch" make savedefconfig)

# 6. Commit this defconfig into the tree.
export GIT_AUTHOR_NAME="DH-Electronics Ci"
export GIT_AUTHOR_EMAIL="none@none"
export GIT_COMMITTER_NAME="$GIT_AUTHOR_NAME"
export GIT_COMMITTER_EMAIL="$GIT_AUTHOR_EMAIL"

mv "$sourcedir/defconfig" "$sourcedir/$defconfig_name"
git -C "$sourcedir" add "$defconfig_name"
git -C "$sourcedir" commit -F - <<EOF
ARM: configs: Add defconfig for DH-Electronics

This config was automatically extracted from $(basename "$(realpath .)")[1]
at commit $(git -c core.abbrev=12 show --pretty=format:'%h ("%s")' --no-patch).

[1]: $(git remote get-url origin)
Upstream-Status: Inappropriate [configuration]
EOF

timestamp="$(date --rfc-3339=date)"
# 7. Tag this revision with a tag by date and git-hash from the layer repo.
git -C "$sourcedir" tag -a -F - "dh-${timestamp}-g$(git -c core.abbrev=12 show --pretty=format:'%h' --no-patch)" <<EOF
DH-Electronics release on ${timestamp}
EOF

# 8. Push to other repository.
echo "Staged sources are in $sourcedir/"
