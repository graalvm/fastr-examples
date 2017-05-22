if [ -z "$GRAALVM_DIR" ]; then
  echo "Please download GraalVM and set environment variable 'GRAALVM_DIR' to its location"
  exit
fi
$GRAALVM_DIR/bin/java -cp bin com.oracle.truffle.r.fastrjavaui.FastRJavaUI
