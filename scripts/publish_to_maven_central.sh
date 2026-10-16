#!/bin/bash
# This script uploads a zip file to Maven Central.
# It expects the current working directory to be the one where the build folder is located (e.g. core)
# This build folder should contain the local-repo folder with the artefacts to be zipped and uploaded.
set -e

subproject=$1
version=$2

echo "Running $(basename $0) for subproject \"$subproject\" and version \"$version\""
echo "Executing in directory = $(pwd)"

zipfile=${subproject}.${version}.zip

# Ensure jq is available if later used by callers; not strictly required here but helpful to surface missing deps early.
if ! command -v jq >/dev/null 2>&1; then
  echo "Warning: jq not found. Some scripts expect jq for JSON parsing. Continue if you're sure it's not needed."
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

# Upload to maven central.
# publishingType=USER_MANAGED means that the artefacts will be uploaded and verified, but not yet published.
# See https://central.sonatype.com/publishing/deployments (you need to login as mobilitydata)
# From this page you can examine the list of artefacts, and either drop them or release them.
# If you want to remove that step, use publishingType=AUTOMATIC below, that will publish directly.
# Note that once published you cannot remove or alter the artifacts.
answer=$(curl --request POST --silent --show-error --header "${auth_header}" --form bundle="@${zipfile}" 'https://central.sonatype.com/api/v1/publisher/upload?publishingType=USER_MANAGED')

echo "curl request answer: $answer"

popd >/dev/null