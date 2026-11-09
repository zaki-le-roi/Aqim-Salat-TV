#!/usr/bin/env bash
set -Eeuo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RAW_DIR="$ROOT/android/app/src/main/res/raw"
WORK_DIR="${TMPDIR:-/tmp}/aqim-salat-audio"
mkdir -p "$RAW_DIR" "$WORK_DIR"
command -v ffmpeg >/dev/null || { echo "ffmpeg is required" >&2; exit 1; }
command -v ffprobe >/dev/null || { echo "ffprobe is required" >&2; exit 1; }
command -v curl >/dev/null || { echo "curl is required" >&2; exit 1; }

download_and_convert() {
  local url="$1" page="$2" credit="$3" license="$4" source_file="$5"
  local output="$RAW_DIR/adhan_default.ogg"
  rm -f "$output" "$source_file"
  echo "Trying: $page ($license)"
  if ! curl --fail --location --retry 1 --connect-timeout 8 --max-time 120 \
      --user-agent "AqimSalatTV/0.1 (open-license audio import)" "$url" -o "$source_file"; then
    return 1
  fi
  if ! ffmpeg -hide_banner -loglevel error -y -i "$source_file" -vn \
      -af "silenceremove=start_periods=1:start_duration=0.15:start_threshold=-55dB:stop_periods=1:stop_duration=1:stop_threshold=-55dB,loudnorm=I=-18:TP=-1.5:LRA=11" \
      -ac 1 -ar 44100 -c:a libvorbis -q:a 4 "$output"; then
    rm -f "$output"
    return 1
  fi
  local duration has_audio
  duration="$(ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 "$output")"
  has_audio="$(ffprobe -v error -select_streams a:0 -show_entries stream=codec_type -of csv=p=0 "$output")"
  if [[ "$has_audio" != "audio" ]] || ! awk -v d="$duration" 'BEGIN { exit !(d >= 120 && d <= 240) }'; then
    echo "Rejected audio: expected 120-240 seconds, got ${duration}s" >&2
    rm -f "$output"
    return 1
  fi
  printf 'VERIFIED_AUDIO=1\nDURATION_SECONDS=%s\nSOURCE_PAGE=%s\nCREDIT=%s\nLICENSE=%s\n' \
    "$duration" "$page" "$credit" "$license"
  return 0
}

# Preferred candidate: Creative Commons Attribution 3.0 (no share-alike obligation).
if download_and_convert \
  "https://commons.wikimedia.org/wiki/Special:Redirect/file/Adhan,_Great_Mosque_of_Mecca_-_Jan_21,_2013.webm" \
  "https://commons.wikimedia.org/wiki/File:Adhan,_Great_Mosque_of_Mecca_-_Jan_21,_2013.webm" \
  "Seyfula Islam" "CC BY 3.0" "$WORK_DIR/adhan_cc_by.webm"; then
  exit 0
fi

# Secondary candidate: CC BY-SA 4.0. Use only if the preferred CC BY recording cannot be fetched.
if download_and_convert \
  "https://commons.wikimedia.org/wiki/Special:Redirect/file/Adhan_in_Shalqar_mosque.webm" \
  "https://commons.wikimedia.org/wiki/File:Adhan_in_Shalqar_mosque.webm" \
  "Esetok" "CC BY-SA 4.0" "$WORK_DIR/adhan_cc_by_sa.webm"; then
  exit 0
fi

rm -f "$RAW_DIR/adhan_default.ogg"
echo "No full-length licensed adhan could be fetched; generating an original short alert tone." >&2
ffmpeg -hide_banner -loglevel error -y -f lavfi -i "sine=frequency=880:duration=2.5" \
  -af "afade=t=out:st=1.8:d=0.7,loudnorm=I=-18:TP=-1.5:LRA=11" \
  -ac 1 -ar 44100 -c:a libvorbis -q:a 3 "$RAW_DIR/aqim_alert_tone.ogg"
echo "FALLBACK_TONE=1"
echo "Generated: $RAW_DIR/aqim_alert_tone.ogg"
