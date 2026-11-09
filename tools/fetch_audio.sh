#!/usr/bin/env bash
set -Eeuo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RAW_DIR="$ROOT/android/app/src/main/res/raw"
WORK_DIR="${TMPDIR:-/tmp}/aqim-salat-audio"
SOURCE_PAGE="https://commons.wikimedia.org/wiki/File:Adhan_in_Shalqar_mosque.webm"
SOURCE_URL="https://commons.wikimedia.org/wiki/Special:Redirect/file/Adhan_in_Shalqar_mosque.webm"

mkdir -p "$RAW_DIR" "$WORK_DIR"
command -v ffmpeg >/dev/null || { echo "ffmpeg is required" >&2; exit 1; }
command -v ffprobe >/dev/null || { echo "ffprobe is required" >&2; exit 1; }

SOURCE_FILE="$WORK_DIR/adhan_source.webm"
OUTPUT="$RAW_DIR/adhan_default.ogg"

if curl --fail --location --retry 2 --user-agent "AqimSalatTV/0.1 (open licensed audio import)" "$SOURCE_URL" -o "$SOURCE_FILE"; then
  if ffmpeg -hide_banner -loglevel error -y -i "$SOURCE_FILE" -vn \
      -af "silenceremove=start_periods=1:start_duration=0.15:start_threshold=-55dB:stop_periods=1:stop_duration=1:stop_threshold=-55dB,loudnorm=I=-18:TP=-1.5:LRA=11" \
      -ac 1 -ar 44100 -c:a libvorbis -q:a 4 "$OUTPUT"; then
    DURATION="$(ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 "$OUTPUT")"
    HAS_AUDIO="$(ffprobe -v error -select_streams a:0 -show_entries stream=codec_type -of csv=p=0 "$OUTPUT")"
    if [[ "$HAS_AUDIO" == "audio" ]] && awk -v d="$DURATION" 'BEGIN { exit !(d >= 120 && d <= 240) }'; then
      echo "Verified audio stream and duration: ${DURATION}s"
      echo "Source: $SOURCE_PAGE"
      echo "Creator: Esetok | License: CC BY-SA 4.0"
      exit 0
    fi
  fi
fi

rm -f "$OUTPUT"
echo "No verified full-length recording could be fetched; generating a short alert tone instead." >&2
ffmpeg -hide_banner -loglevel error -y -f lavfi -i "sine=frequency=880:duration=2.5" \
  -af "afade=t=out:st=1.8:d=0.7,loudnorm=I=-18:TP=-1.5:LRA=11" \
  -ac 1 -ar 44100 -c:a libvorbis -q:a 3 "$RAW_DIR/aqim_alert_tone.ogg"
echo "Generated fallback: $RAW_DIR/aqim_alert_tone.ogg"
