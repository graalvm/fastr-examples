#!/bin/bash
# Run demo with tools enabled and with exceptions being printed to command line
$GRAALVM_DIR/bin/node ${GRAALVM_ADDITIONAL_ARGS} --jvm --jvm.Xss2m --jvm.Dtruffle.js.NashornJavaInterop=true --polyglot --inspect --inspect.Suspend=false --agent server.js

