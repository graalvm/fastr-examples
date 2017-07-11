#!/bin/bash
export NODE_STACK_SIZE=2000000
$GRAALVM_DIR/bin/node --jvm --jvm.Dtruffle.js.NashornJavaInterop=true --jvm.classpath=./bin --jvm.Dtruffleruby.load_paths=openweather/lib,. weatherServer.js
