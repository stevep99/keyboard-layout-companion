
Keyboard Layout Companion is a tool for visualizing and generating images of alternative keyboard layouts and designs.

##### Layouts

Keyboard layouts are mappings between the physical key and the output that is produced when that key is typed. A wide variety of alternative layouts are included by default, but you can also add your own. Each layout can have one or more _layers_ defined. If more than one layer is defined in a layout, controls will be shown making it possible to cycle through the available layers.

To add extra layouts (Android version only), create a layout definition text file on the SD Card in the location:  
`Android/data/io.github.colemakmods.keyboard_companion/files/layouts`

##### Keyboard Geometries

A number of common keyboard physical geometries are supported, including ANSI (common in North America), ISO (common in Europe), and JIS (common in Japan). Also supported are a selection of matrix-like keyboards. You can also create custom keyboard geometries.

The app also allows you to view several variations of the most common keyboard geometries, including the [Wide Mod](https://colemakmods.github.io/ergonomic-mods/wide.html) and [Angle Mod](https://colemakmods.github.io/ergonomic-mods/angle.html).

To add extra geometries (Android version only), create a geometry definition JSON file on the SD Card in the location:  
`Android/data/io.github.colemakmods.keyboard_companion/files/geometries`

##### Editing

Click on a key to edit its label and finger assignment. You can also swap the positions of two keys using drag-and-drop on the keyboard view. For more detailed editing, such as adding layers or adjusting key colours, a keyboard definition file must be added.

##### Other Options

The "Filter" dropdown allows only a chosen portion of the keyboard to be rendered.

The "Colors" control allows the keys to be colour-coded by finger assignment or by function. The "function" selection can overridden in the layout definition file.

The "Split" checkbox will render a version of the keyboard with left- and right-hand sides split. The spacebar will also be split into two in this case.

The "Styles" checkbox allows any extra styling on the keys to be shown, such as home key indicators or fading.

The Title Bar contains buttons to print the current keyboard to an image file, and to write the current layout as a text file. On the Web version the image will be downloaded, and on Android it is saved on the SDCard in the location:  
`Android/data/io.github.colemakmods.keyboard_companion/files/output`

##### Modes

The app has an optional "Modes" feature, which can be turned on in the settings. There are four different modes selectable via the _Mode_ dropdown:
- **Layout:** the default mode, for visualizing and editing keyboard layouts.
- **Score:** for calculating an "effort grid" for a keyboard for use in Keyboard Analyzer program. The algorithm used for the calculation is defined in the [Colemak-DH Model](https://colemakmods.github.io/mod-dh/model.html) page.
- **Distance:** for calculating the travel distance for each key for its respective home finger, as measured using the algorithm described above.
- **Key ID:** an internal Key ID reference.

##### About

This app was written by SteveP ©2015-2025.  
Released under GNU GPL Version 3, May 2021. See the COPYING file in the project's repository.  
Adapted for Kotlin Multiplatform (wasm target) August 2025.
