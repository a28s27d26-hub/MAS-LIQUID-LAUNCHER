# MAS LIQUID LAUNCHER

Native Android home-screen launcher built with Kotlin and Jetpack Compose.

## Current foundation
- Android HOME app entry point; users can select it as their default launcher.
- Installed app discovery and launching.
- Searchable app drawer.
- Glass-inspired translucent surfaces and gradient background.
- GitHub Actions workflow for debug APK builds.

## Build
Pushes to `main` trigger the Android workflow. Download the APK from the successful run's Artifacts section.

## Performance
This foundation uses lightweight translucent surfaces and gradients rather than expensive real-time blur. Further visual effects should be profiled on target devices before enabling them broadly.
