# Runs the demo without any tools enabled and printing all exceptions to terminal
#!/bin/bash
$GRAALVM_DIR/bin/node ${GRAALVM_ADDITIONAL_ARGS} --jvm --jvm.Xss2m --jvm.Dtruffle.js.NashornJavaInterop=true --jvm.classpath=./bin --ruby.load_paths=openweather/lib,. --polyglot  weatherServer.js
