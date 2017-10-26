#!/bin/bash
echo Testing GraalVM fastr_javaui example
rm -f test.png
if ! $GRAALVM_DIR/bin/java -cp bin com.oracle.truffle.r.fastrjavaui.FastRJavaCmd | grep "SUCCESS" > /dev/null; then
  echo "expected output not found"
  exit 1
fi
if [ ! -f test.png ]; then
  echo "expected file not generated"
  exit 1
fi
echo DONE
