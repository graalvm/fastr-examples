if [ -z "$GRAAL_VM" ]; then
  echo "Please download GraalVM and set environment variable 'GRAAL_VM' to its location"
  exit
fi
mkdir -p bin
cd src/main/java/
$GRAAL_VM/bin/javac -cp $GRAAL_VM/lib/truffle/truffle-api.jar -d ../../../bin com/oracle/truffle/r/fastrjavaui/FastRJavaUI.java
