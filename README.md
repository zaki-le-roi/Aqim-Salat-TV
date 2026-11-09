# Aqim Salat TV

Global offline-first prayer times and adhan for Android TV and Google TV.

## Technology baseline
- Flutter stable 3.47.9 / Dart 3.13.7
- Dart UI and application logic; Android platform integration written in Java
- Riverpod 3.5.0, Drift 2.35.4, shared_preferences 2.5.6
- adhan_dart 2.0.1 and the IANA timezone database
- Android TV Leanback launcher, D-pad-first UI, minSdk 21, targetSdk 34

## Verified implementation status
The repository currently contains the Flutter app entry point, 11 ARB locale files, Android TV manifest/resources, Java MethodChannel, exact-alarm scheduler, timezone-aware alarm restoration receiver, and a short runtime ToneGenerator fallback. The scheduler accepts city-local wall-clock times with an IANA zone ID and resolves them to instants in Java, including DST transitions.

This is an **early implementation stage, not a complete release**. Prayer-time calculation, country/city database, OSM mosque search, Drift schema and repositories, full settings, real licensed adhan recordings, full-screen adhan UI, and seven-day Dart scheduling still need implementation and device/build verification.

## Build
Use Flutter 3.47.9 stable and JDK 17:
```sh
flutter pub get
flutter gen-l10n
flutter test
flutter analyze
flutter build apk --release
```

The CI workflow creates the Gradle wrapper before building. For a local build from a fresh clone, generate it once from the `android` directory with Gradle 8.14.3 after Flutter has generated `android/local.properties`.

## Architecture
- `lib/`: Flutter UI, domain logic, data access, and localization.
- `android/`: Android TV manifest and Java-only alarm/service integration.
- `assets/`: offline datasets and font files (to be added).
- `tools/`: reproducible dataset/audio acquisition scripts.

## Data and audio policy
OpenStreetMap data is attributed to OpenStreetMap contributors and subject to ODbL. Public Nominatim and Overpass instances are community services with usage policies and no uptime guarantee. No third-party audio recording is currently bundled. The current Java service emits a short system-generated tone only; full adhan files will be added only after verifying each recording's exact license and attribution requirements.
