#!/bin/bash
#
# Helper script for constructing a Mac OS icns icon bundle,
# from an intermediate iconset file bundle.  Requires install
# of ImageMagik for `convert` command.

set -e

BASEDIR=$(dirname "$0")
SVG=$BASEDIR/Icon.svg
ICONSET_DIR=$BASEDIR/Icon.iconset

CONFIGS="
icon_16x16.png,16x16
icon_16x16@2x.png,32x32
icon_32x32.png,32x32
icon_32x32@2x.png,64x64
icon_128x128.png,128x128
icon_128x128@2x.png,256x256
icon_256x256.png,256x256
icon_256x256@2x.png,512x512
icon_512x512.png,512x512
icon_512x512@2x.png,1024x1024
"

if [ -e $ICONSET_DIR ]; then
  rm -rf "$ICONSET_DIR"
fi
mkdir $ICONSET_DIR

for CONFIG in $CONFIGS; do
  FILENAME=$(echo $CONFIG | cut -d, -f1)
  SIZE=$(echo $CONFIG | cut -d, -f2)
  echo $FILENAME
  convert -density 1200 -background none -resize $SIZE $SVG $ICONSET_DIR/$FILENAME
done

iconutil -c icns $ICONSET_DIR
rm -rf "$ICONSET_DIR"
