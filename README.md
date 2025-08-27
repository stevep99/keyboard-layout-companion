
# Keyboard Layout Companion

Keyboard Layout Companion is a tool for visualizing and generating images of alternative keyboard layouts and designs.

**[View Web Version](https://stevep99.github.io/keyboard-layout-companion)**

**[Download from Google Play](http://play.google.com/store/apps/details?id=io.github.colemakmods.keyboard_companion)**

**[Download APK direct](https://github.com/stevep99/keyboard-layout-companion/releases)**

See the [info](https://github.com/stevep99/keyboard-layout-companion/blob/master/app/src/commonMain/composeResources/files/info.md) page for details.

## Building

### For Android

Prerequisites:

- Install the Android SDK

Compile:

- `gradlew assembleDebug`

The compiled Android application package kit (APK) is built in `app/build/outputs/apk`.

### For Web (wasm)

- `./gradlew clean wasmJsBrowserDevelopmentRun  -t`

