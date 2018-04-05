# A small benchmark script exercising rJava functionality"

## Setup

Download the latest GraalVM image from the [Oracle Technology Network](http://www.oracle.com/technetwork/oracle-labs/program-languages/downloads/index.html).
Extract the archive and set the `GRAALVM_DIR` environment variable to point to the graalvm directory.

Additionally, to run this applicationl, you need to install [rJava for FastR](https://github.com/oracle/fastr/tree/master/com.oracle.truffle.r.pkgs/rJava).

To install rJava, to you need to download the FastR repo from e.g. https://github.com/oracle/fastr and then isntall it for `R`: 

Something like the following snippet should do the trick: 
```
git clone https://github.com/oracle/fastr
cd fastr
$GRAALVM_DIR/bin/R CMD INSTALL ./com.oracle.truffle.r.pkgs/rJava
```


## Run

Execute the `run.sh` script or `${GRAALVM_DIR}/bin/Rscript rJavaBench.R`
