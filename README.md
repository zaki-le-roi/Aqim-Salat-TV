# Aqim Salat TV

Global offline-first prayer times and adhan for Android TV and Google TV.

## Technology baseline
- Flutter stable 3.47.9 / Dart 3.13.7
- Dart UI and application logic; Android platform integration in Java only
- Riverpod 3.5.0, Drift 2.35.4, shared_preferences 2.5.6
- adhan_dart 2.0.1 and IANA timezone database
- Android TV Leanback launcher and D-pad-first navigation

## Project status
The initial scaffold is committed. It establishes localization, the TV launcher manifest, a Java method channel, dependency pins, and a CI build workflow. It is **not yet a complete prayer-time or adhan release**. City database ingestion, prayer calculation, mosque search, persistent scheduling, audio assets, and the remaining eight translations are staged work.

## Build
Use Flutter 3.47.9 stable and JDK 17:
```sh
flutter --version
flutter pub get
flutter gen-l10n
flutter analyze
flutter build apk --release
```

## Architecture
- `lib/`: Flutter UI, domain logic, data access, and localization.
- `android/`: Android TV manifest and Java-only alarm/service integration.
- `assets/`: bundled datasets and fonts.
- `tools/`: reproducible dataset/audio acquisition scripts and license records.

## Data and audio policy
OpenStreetMap data is attributed to OpenStreetMap contributors and subject to ODbL. Public Nominatim and Overpass instances are community services with usage policies and no uptime guarantee. Audio is included only after its exact source and license are verified; no copyrighted reciter recordings are bundled by default.
