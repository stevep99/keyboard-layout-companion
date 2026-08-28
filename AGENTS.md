# AGENTS.md

## Project Overview

**Keyboard Layout Companion** is a Kotlin Multiplatform (KMP) application for visualizing
and generating images of alternative keyboard layouts and designs. It targets **Android**
and **Web (Kotlin/Wasm)** from a shared `commonMain` source set.

- Package: `io.github.colemakmods.keyboard_companion`
- UI: [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform)
- Build system: Gradle (Kotlin DSL) with a version catalog

## Repository Layout

- `settings.gradle.kts` — includes the `:app` module; configures plugin/repository management.
- `build.gradle.kts` — root build file applying plugins with `apply(false)`.
- `gradle/libs.versions.toml` — **single source of truth for all dependency versions.**
- `app/build.gradle.kts` — module-level build config (Android + wasmJs targets, source sets, deps).
- `app/src/`
  - `commonMain/` — shared UI, models, and Compose resources (`composeResources/`).
  - `androidMain/`, `wasmJsMain/`, `iosMain/` — platform-specific code.
  - `commonTest/`, `androidUnitTest/`, `iosTest/` — tests.

## Dependency Management (IMPORTANT)

All version numbers live in `gradle/libs.versions.toml`. When changing a dependency:

- Update the version in `[versions]` and let `[libraries]`/`[plugins]` refs flow through.
- **Compose Multiplatform**, its Jetpack Compose mapping, and the JetBrains
  `androidx.lifecycle` artifacts are released together — keep them aligned:
  - `compose` → `org.jetbrains.compose` plugin + `androidx.compose.*` libraries
  - `androidx-lifecycle` → `org.jetbrains.androidx.lifecycle` (must match the Compose version)
- The Kotlin version (`kotlin`) must match the `kotlin("plugin.serialization")` and
  `org.jetbrains.kotlin.plugin.compose` plugin versions.

### Current key versions

| Item | Version |
| --- | --- |
| Compose Multiplatform | `1.12.0` (Jetpack Compose `1.12.0`) |
| Kotlin | `2.4.10` (its bundled Compose compiler must match CMP) |
| JetBrains AndroidX Lifecycle | `2.11.0` |
| Android Gradle Plugin | `9.1.0` |
| multiplatform-markdown-renderer | `0.44.0` |
| Gradle | `9.3.1` |
| compileSdk / targetSdk | `37` |
| minSdk | `23` (required by CMP `components-resources`) |

## Common Commands

Use the Gradle wrapper (`./gradlew`).

- Build Android APK (debug): `./gradlew assembleDebug` → `app/build/outputs/apk`
- Run Web (wasm) dev server with watch: `./gradlew clean wasmJsBrowserDevelopmentRun -t`
- Build/run: `./gradlew build`
- Refresh the version catalog / dependencies: `./gradlew dependencies`

## Conventions

- Shared code goes in `commonMain`; avoid platform-specific APIs there.
- Keyboard layout/geometry definitions live under
  `app/src/commonMain/composeResources/files/` (text + JSON resources).
- The in-app info page content is `composeResources/files/info.md`.
- Prefer editing the version catalog over hardcoding versions inline.

## Upgrading Compose Multiplatform

Compose Multiplatform `1.12.0` carries hard requirements that cascade into several
other version bumps. When upgrading, update **all** of the following together:

1. `compose` → new version in `gradle/libs.versions.toml` (also drives `compose-ui-tooling`).
2. `kotlin` → a version whose **bundled Compose compiler** supports the new CMP release
   (CMP 1.12.0 needs Kotlin `2.4.x`). The `org.jetbrains.kotlin.plugin.compose`
   plugin version must stay equal to `kotlin`. Using an older Kotlin triggers a
   `FirRegularClassSymbol` NullPointerException in `compileKotlinWasmJs`.
3. `androidx-lifecycle` → version shipped by that Compose release
   (see the JetBrains release notes "Components" table; CMP 1.12.0 → `2.11.0`).
4. AGP → `9.1.0`+ (CMP 1.12.0's Android AARs require `minAgpVersion = 9.1.0` and
   `minCompileSdk = 37`). Bumping AGP requires Gradle `9.3.1`+ and `compileSdk`/`targetSdk` `37`.
5. `minSdk` → at least `23` (CMP `components-resources` declares `minSdk 23`).
6. Compose-dependent libraries (e.g. `multiplatform-markdown-renderer`) → a release built
   against the new Compose version.
7. Because AGP 9.x forbids `com.android.application` + the Kotlin Multiplatform plugin,
   `gradle.properties` sets `android.builtInKotlin=false` and `android.newDsl=false` to keep
   the legacy `android { }` DSL working. If you migrate to the new
   `com.android.kotlin.multiplatform.application` plugin, remove those flags.
8. Verify both targets build: `./gradlew assembleDebug wasmJsBrowserDistribution`.
   An Android SDK Platform `37` must be installed (`sdkmanager "platforms;android-37"`).
