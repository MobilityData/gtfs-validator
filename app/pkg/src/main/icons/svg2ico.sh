#!/bin/bash
#
# Helper script for constructing a Windows icon bundle.  Requires
# install of ImageMagik for `convert` command.

set -e

BASEDIR=$(dirname "$0")
SVG=$BASEDIR/Icon.svg
ICO_DIR=$BASEDIR/Icon.ico_dir

CONFIGS="
icon_16x16.png,16x16
icon_32x32.png,32x32
icon_48x48.png,48x48
icon_64x64.png,64x64
icon_128x128.png,128x128
icon_256x256.png,256x256
"

if [ -e $ICO_DIR ]; then
  rm -rf "$ICO_DIR"
fi
mkdir $ICO_DIR

for CONFIG in $CONFIGS; do
  FILENAME=$(echo $CONFIG | cut -d, -f1)
  SIZE=$(echo $CONFIG | cut -d, -f2)
  echo $FILENAME
  convert -density 1200 -background none -resize $SIZE $SVG $ICO_DIR/$FILENAME
done

convert $ICO_DIR/*.png Icon.ico
rm -rf "$ICO_DIR"
