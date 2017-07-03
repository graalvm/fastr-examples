#!/bin/bash
export NODE_STACK_SIZE=2000000
$GRAALVM_DIR/bin/node -cp ./bin -J-Dtruffleruby.load_paths=openweather/lib,. weatherServer.js
