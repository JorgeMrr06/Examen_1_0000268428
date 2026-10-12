#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p build/classes dist
javac --release 17 -encoding UTF-8 -d build/classes ./*.java
jar --create --file dist/PagoLuz.jar --main-class mx.itson.luz.app.AplicacionLuz -C build/classes .

