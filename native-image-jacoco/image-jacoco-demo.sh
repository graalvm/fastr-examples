#!/bin/bash
set -e
if [[ ! ${GRAALVM_HOME+x} ]]; then
  echo "GRAALVM_HOME not set."
  exit 1
fi
echo ""
echo "create image from instrumented classes"
echo ""
./gradlew aotInstrumentedImage
echo ""
echo "compile and link test with isolates"
echo ""
./gradlew compileTest
echo ""
echo "run test with isolates"
echo ""
./gradlew runTestWithInstrumentation
echo ""
echo "generate report"
echo ""
./gradlew report
echo ""
echo "create image from non-instrumented classes"
echo ""
./gradlew aotImage
echo ""
echo "run test without instrumentation"
echo ""
./gradlew runTest
