#!/bin/bash
# This script manages the uploading of snapshots to the Maven Central snapshot repository, and deletion of the associated snapshots after a set retention time.
set -e

subproject=$1
version=$2

echo "Running $(basename $0) for subproject \"$subproject\" and version \"$version\""
echo "Executing in directory = $(pwd)"

zipfile=${subproject}.${version}.zip

# Ensure jq is available for JSON parsing
if ! command -v jq >/dev/null 2>&1; then
  echo "Error: jq is required but not installed. Please install jq and retry."
  exit 1
fi

pushd build/local-repo >/dev/null
zip -qr "${zipfile}" *

# Build Authorization header: prefer an explicit bearer token, otherwise fall back to Basic auth built from username/password
if [ -n "${MAVEN_CENTRAL_PORTAL_BEARER_TOKEN:-}" ]; then
  auth_header="Authorization: Bearer ${MAVEN_CENTRAL_PORTAL_BEARER_TOKEN}"
elif [ -n "${MAVEN_CENTRAL_PORTAL_TOKEN_USERNAME:-}" ] && [ -n "${MAVEN_CENTRAL_PORTAL_TOKEN_PASSWORD:-}" ]; then
  basic_token=$(echo -n "${MAVEN_CENTRAL_PORTAL_TOKEN_USERNAME}:${MAVEN_CENTRAL_PORTAL_TOKEN_PASSWORD}" | base64 | tr -d '\n')
  auth_header="Authorization: Basic ${basic_token}"
else
  echo "Error: No credentials found. Set MAVEN_CENTRAL_PORTAL_BEARER_TOKEN or MAVEN_CENTRAL_PORTAL_TOKEN_USERNAME and MAVEN_CENTRAL_PORTAL_TOKEN_PASSWORD."
  popd >/dev/null || true
  exit 1
fi

# Delete older snapshots before uploading the newest one to the project's snapshot repository on Maven Central.
# Query the deployments/files endpoint and delete each returned deployment id.
response=$(curl -s --request POST --header "${auth_header}" 'https://central.sonatype.com/api/v1/publisher/deployments/files')
if [ -z "${response}" ]; then
  echo "No response from Sonatype when listing deployments."
else
  staleIds=$(echo "${response}" | jq -r '.deploymentIds[]?')
  if [ -z "${staleIds}" ]; then
    echo "No stale deployment ids found."
  else
    for id in ${staleIds}; do
      echo "Deleting stale deployment id: ${id}"
      curl -s --request DELETE --header "${auth_header}" "https://central.sonatype.com/api/v1/publisher/deployments/${id}" || echo "Warning: failed to delete deployment ${id}"
    done
  fi
fi

# Publish to the snapshot repository automatically (no manual intervention).
answer=$(curl --request POST --silent --show-error --header "${auth_header}" --form bundle="@${zipfile}" 'https://central.sonatype.com/api/v1/publisher/upload?publishingType=AUTOMATIC')

echo "curl request answer: $answer"

popd >/dev/null
