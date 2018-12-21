JaCoCo Instrumentation for GraalVM Native Images
========================================

This example shows how to get JaCoCo code coverage of GraalVM native images. In the example, we will build shared libraries that use isolates as this is the most compilcated case. The same principles can be used for regular executables.

To run this example we need to set the `GRAALVM_HOME` path to the latest release of GraalVM and execute
```
image-jacoco-demo.sh
```
and find the JaCoCo reports in `reports/jacoco`.

This example builds two images: an ahead-of-time instrumented one (in `build/instrumented/`) and a regular image (in `build/non-instrumented`). The instrumented classes are generated in `classes-instrumented` folder. The test C program with isolates (`native_src/hello.c`) is compiled and ran with generated shared libraries. The profiles are dumped to `jacoco.exec` binary file in the `output` folder.

