#!/bin/bash
$GRAALVM_DIR/bin/node -J-Dtruffleruby.load_paths=openweather/lib,. weatherServer.js
