#!/bin/bash
$GRAALVM_DIR/bin/node --jvm --jvm.Xss2m --jvm.Dtruffle.js.NashornJavaInterop=true --jvm.classpath=./bin --ruby.load_paths=openweather/lib,. --polyglot weatherServer.js
