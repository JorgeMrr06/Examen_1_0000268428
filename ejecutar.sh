#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
if [ ! -f dist/PagoLuz.jar ]; then sh build.sh; fi
java -jar dist/PagoLuz.jar "$@"

