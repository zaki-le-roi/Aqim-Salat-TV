# Asset licenses

No third-party audio recordings, city database, font binaries, or image assets are bundled in this stage.

| Asset | File / implementation | Source | License | Status |
|---|---|---|---|---|
| Temporary alert tone | Android `ToneGenerator.TONE_PROP_ACK`, generated at runtime by Android | Android platform API; no external recording | No third-party audio file or recording included | Implemented as a short placeholder only; not a full adhan |
| City database | Not bundled | Planned GeoNames population-filtered dataset with timezone IDs | GeoNames CC BY 4.0; attribution required | Not implemented |
| Mosque metadata | Runtime OSM queries planned | OpenStreetMap contributors | ODbL 1.0 attribution/share-alike obligations | Not implemented |

No reciter recording is currently included. A future recording must have a verified item URL, creator, exact license, attribution text, and checksum recorded here before it is bundled or offered for download.
