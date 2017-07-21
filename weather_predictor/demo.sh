#!/bin/bash
# Completely silent execution not reporting any exceptions/errors from Graal
# It is possible to specify --inspect and --inspect.Suspend=false and/or --agent as arguments
$GRAALVM_DIR/bin/node ${GRAALVM_ADDITIONAL_ARGS} --jvm --jvm.Xss2m --jvm.Dtruffle.js.NashornJavaInterop=true --jvm.Dgraal.CompilationFailureAction=Silent --jvm.Djava.util.logging.config.file=java_options --jvm.classpath=./bin --ruby.load_paths=openweather/lib,. --polyglot "$@" weatherServer.js

