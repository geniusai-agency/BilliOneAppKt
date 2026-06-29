#!/usr/bin/env bash
set -euo pipefail

PACKAGE="${1:-com.example.billionemotosappkt}"
PID="$(adb shell pidof -s "$PACKAGE" 2>/dev/null | tr -d '\r')"

if [ -z "$PID" ]; then
  echo "Nao achei o processo de $PACKAGE. Abra o app e tente de novo." >&2
  exit 1
fi

adb logcat --pid "$PID" | grep -vE 'gralloc4|dataspace|OpenGLRenderer|HWUI|RenderThread|MIUIInput'
