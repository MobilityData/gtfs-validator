#!/bin/bash
set -x

subproject=$1
version=$2
# https://central.sonatype.org/publish/publish-portal-upload/

echo "MAVEN_CENTRAL_PORTAL_USERNAME = $MAVEN_CENTRAL_PORTAL_USERNAME"
echo "subproject = $subproject"

zipfile=${subproject}.${version}.zip

pushd build/local-repo
zip -r ${zipfile} io

bearer_token=$(echo "$MAVEN_CENTRAL_PORTAL_TOKEN_USERNAME:$MAVEN_CENTRAL_PORTAL_TOKEN_PASSWORD" | base64)

answer=$(curl --request POST \
  --verbose \
  --header "Authorization: Bearer ${bearer_token}" \
  --form bundle="@${zipfile}" \
  'https://central.sonatype.com/api/v1/publisher/upload?publishingType=USER_MANAGED')

echo "curl request answer: $answer"

popd