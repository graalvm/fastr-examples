if [ -z "$GRAALVM_DIR" ]; then
  echo "Please download GraalVM and set environment variable 'GRAALVM_DIR' to its location"
  exit
fi
mkdir -p bin
cd src/main/java/
$GRAALVM_DIR/bin/javac -d ../../../bin com/oracle/truffle/r/fastrjavaui/FastRJavaUI.java
$GRAALVM_DIR/bin/javac -d ../../../bin com/oracle/truffle/r/fastrjavaui/FastRJavaCmd.java
