#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
sh build.sh
java -Djava.awt.headless=true -cp build/classes mx.itson.luz.pruebas.DemoSwingLuz build/evidencias

