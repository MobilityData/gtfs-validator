#!/bin/bash
# This script manages the uploading of snapshots to the Maven Central snapshot repository, and deletion of the associated snapshots after a set retention time.
set -e

subproject=$1
version=$2

echo "Running $(basename $0) for subproject \"$subproject\" and version \"$version\""
echo "Executing in directory = $(pwd)"

zipfile=${subproject}.${version}.zip

pushd build/local-repo
zip -qr ${zipfile} *

bearer_token=$(echo "$MAVEN_CENTRAL_PORTAL_TOKEN_USERNAME:$MAVEN_CENTRAL_PORTAL_TOKEN_PASSWORD" | base64)

# We delete older snapshots before uploading the new one to Maven Central's snapshot repository.
#TODO: this does not iterate properly into the for. fix
for deployments in $subproject; do
getId=$(curl -s --request POST \
  --header "Authorization: Bearer ${bearer_token}" \
  'https://central.sonatype.com/api/v1/publisher/deployments/files')
staleId=$(getId | jq -r '.deploymentIds')
echo "id from old snapshot: $staleId"

dropStale=$(curl --request DELETE \
  --verbose \
  --header "Authorization: Bearer ${bearer_token}" \
  "https://central.sonatype.com/api/v1/publisher/deployments/{$staleId}")
done

# We publish to the snapshot repository automatically whenever this script is called, without manual intervention.
answer=$(curl --request POST \
  --verbose \
  --header "Authorization: Bearer ${bearer_token}" \
  --form bundle="@${zipfile}" \
  'https://central.sonatype.com/api/v1/publisher/upload?publishingType=AUTOMATIC')

echo "curl request answer: $answer"

popd