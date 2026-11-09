# Asset licenses and audio acquisition status

The audio acquisition script prefers CC BY recordings over CC BY-SA. It checks that a downloaded file contains an audio stream and is between 120 and 240 seconds, then trims leading/trailing silence, normalizes loudness, and converts it to mono OGG/Vorbis. This technical validation does not prove the spoken content or verify the Fajr phrase; a human listening review is still required.

| Asset | Source / creator | License | Attribution / obligations | Status |
|---|---|---|---|---|
| Preferred general adhan candidate | [Adhan, Great Mosque of Mecca - Jan 21, 2013](https://commons.wikimedia.org/wiki/File:Adhan,_Great_Mosque_of_Mecca_-_Jan_21,_2013.webm), Seyfula Islam | CC BY 3.0 | Credit Seyfula Islam, link the source and CC BY 3.0, note audio extraction/conversion. No share-alike requirement. | Scripted first choice; download not yet confirmed in this environment |
| Secondary general adhan candidate | [Adhan in Shalqar mosque](https://commons.wikimedia.org/wiki/File:Adhan_in_Shalqar_mosque.webm), Esetok | CC BY-SA 4.0 | Credit Esetok, link source and license, note conversion. Adapted recording must remain under CC BY-SA 4.0 or compatible terms. | Scripted second choice only |
| CC0 candidate not yet automated | [Call_To_Prayer_Loud_Fes_Morocco](https://freesound.org/people/SignatureSoundsOrg/sounds/869669/), SignatureSoundsOrg | CC0 | No attribution legally required; credit is still welcome. 3:08.025 duration. Freesound page currently requires login to download, so it is not yet a reproducible unauthenticated build input. | Investigated; not bundled |
| Temporary alert | `aqim_alert_tone.ogg`, generated from an 880 Hz sine wave by FFmpeg | Original generated tone | No third-party recording or attribution | Script fallback. Locally generated and probed successfully: OGG/Vorbis, mono, 44.1 kHz, 2.5 seconds, 8,992 bytes. Not a full adhan. |

The current source repository does not yet contain a verified full-length adhan binary. In the isolated execution environment used for testing, DNS access to Wikimedia Commons failed, so the script generated and validated the short fallback tone instead. No recording has been claimed as downloaded or listened to.

No Fajr-specific recording with the phrase “الصلاة خير من النوم” and no five-reciter set has been verified or bundled. The About screen must display the source/creator/license of whichever downloaded recording is actually packaged; for CC BY-SA, it must explicitly show the share-alike condition.
