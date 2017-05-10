if [ -z "$GRAAL_VM" ]; then
  echo "Please download GraalVM and set environment variable 'GRAAL_VM' to its location"
  exit
fi
$GRAAL_VM/bin/java -cp bin com.oracle.truffle.r.fastrjavaui.FastRJavaUI
