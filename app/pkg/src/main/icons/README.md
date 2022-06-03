## Updating Application Icons

The source file for all application icons is `Icon.svg`.  This is used to create three
platform-specific application icons:

* Windows: `Icon.ico`
* Mac OS: `Icon.icns`
* Java In-App

When updating the source icon, you can run the following three scripts to regenerate the
relevant icon files from the SVG source:

* `svg2ico.sh`
* `svg2icsn.sh`
* `svg2jframe_icons.sh`