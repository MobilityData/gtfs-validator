#!/bin/bash
# This script uploads a zip file to Maven Central.
# It expects the current working directory to be the one where the build folder is located (e.h. core)
# This build folder should contain the local-repo folder with the artefacts to be zipped and uploaded.
set -e

subproject=$1
version=$2

echo "Running $(basename $0) for subproject \"$subproject\" and version \"$version\""
echo "Executing in directory = $(pwd)"

zipfile=${subproject}.${version}.zip

pushd build/local-repo
zip -qr ${zipfile} *

bearer_token=$(echo "$MAVEN_CENTRAL_PORTAL_TOKEN_USERNAME:$MAVEN_CENTRAL_PORTAL_TOKEN_PASSWORD" | base64)


# Upload to maven central.
# publishingType=USER_MANAGED means that the artefacts will be uploaded and verified, but not yet published.
# See https://central.sonatype.com/publishing/deployments (you need to login as mobilitydata)
# From this page you can examine the list of artefacts, and either drop them or release them.
# If you want to remove that step, use publishingType=AUTOMATIC below, that will publish directly.
# Note that once published you cannot remove or alter the artifacts.
answer=$(curl --request POST \
  --verbose \
  --header "Authorization: Bearer ${bearer_token}" \
  --form bundle="@${zipfile}" \
  'https://central.sonatype.com/api/v1/publisher/upload?publishingType=USER_MANAGED')

echo "curl request answer: $answer"

popd