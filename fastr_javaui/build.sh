if [ -z "$GRAALVM_DIR" ]; then
  echo "Please download GraalVM and set environment variable 'GRAALVM_DIR' to its location"
  exit
fi
mkdir -p bin
cd src/main/java/
$GRAALVM_DIR/bin/javac -cp $GRAALVM_DIR/lib/truffle/truffle-api.jar -d ../../../bin com/oracle/truffle/r/fastrjavaui/FastRJavaUI.java
