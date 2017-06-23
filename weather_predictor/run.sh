#!/bin/bash
$GRAALVM_DIR/bin/node -cp ./bin -J-Dtruffleruby.load_paths=openweather/lib,. weatherServer.js
