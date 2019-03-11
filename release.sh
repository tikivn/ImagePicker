#!/bin/sh

set -e

log() {
  message=$1
  echo "-------------------------------------------------"
  echo "--- $message ---"
  echo "-------------------------------------------------"
}

run() {
  cmd=$1
  log ${cmd}
  ${cmd}
  echo "Total time: $SECONDS secs"
}

# You can run it from any directory.
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_DIR=${DIR}
GRADLE_FILE=${PROJECT_DIR}/gradle.properties

# Get module name
name=$1
echo "name=${name}"

# Get version
version_full=`cat ${GRADLE_FILE} | grep VERSION_NAME=`
echo "version_full=${version_full}"

version=${version_full#*=}
echo "version=${version}"

release_version=${version/-SNAPSHOT/}
echo "release_version=${release_version}"

# Change the version in `gradle.properties` to a non-SNAPSHOT version.
sed -i '' 's/-SNAPSHOT//g' ${GRADLE_FILE}

# `git commit -am "Prepare for release <name> X.Y.Z."` (where X.Y.Z is the new version)
git commit -am "Prepare for release $name $release_version."

tag="$name-$release_version"
echo "tag=$tag"

# `git tag -a <name>-X.Y.Z -m "<Name> vX.Y.Z"` (where X.Y.Z is the new version)
git tag -a ${tag} -m "$name v$release_version"

# `./gradlew clean uploadArchives`
./gradlew clean uploadArchivesS3

# Update the `gradle.properties` to the next SNAPSHOT version.
last_path_version=${release_version##*.}
echo "last_path_version=${last_path_version}"

next_last_path_version=$((last_path_version+1))
echo "next_last_path_version=${next_last_path_version}"

next_dev_version=${version/${last_path_version}-SNAPSHOT/${next_last_path_version}-SNAPSHOT}
echo "next_dev_version=${next_dev_version}"

sed -i '' "s/$release_version/$next_dev_version/g" ${GRADLE_FILE}

# `git commit -am "Prepare next development version."`
git commit -am 'Prepare next development version.'

# `git push && git push --tags`
git push origin HEAD:${name}

git push origin ${tag}
