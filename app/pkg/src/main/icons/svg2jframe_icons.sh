#!/bin/bash
#
# Helper script for constructing Java in-app icon resources.  Requires
# install of ImageMagik for `convert` command.

set -e

BASEDIR=$(dirname "$0")
SVG=$BASEDIR/Icon.svg

PROJECT_ROOT_DIR=$(git rev-parse --show-toplevel)
RESOURCES_DIR=$PROJECT_ROOT_DIR/app/gui/src/main/resources/org/mobilitydata/gtfsvalidator/app/gui

CONFIGS="
icon_16x16.png,16x16
icon_32x32.png,32x32
icon_48x48.png,48x48
"

for CONFIG in $CONFIGS; do
  FILENAME=$(echo $CONFIG | cut -d, -f1)
  SIZE=$(echo $CONFIG | cut -d, -f2)
  echo $FILENAME
  convert -density 1200 -background none -resize $SIZE $SVG $RESOURCES_DIR/$FILENAME
done
