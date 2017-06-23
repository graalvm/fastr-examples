# builds the java sources
mkdir -p bin
cd java
$GRAALVM_DIR/bin/javac -d ../bin com/oracle/graalvm/demo/weather/*.java
