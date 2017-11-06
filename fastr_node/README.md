# FastR Node.js interop example

Demo inspired by Shiny R web applications framework [examples](https://shiny.rstudio.com/gallery/kmeans-example.html).

####Installation

In order to run the demonstration, the latest GraalVM must be installed. It can be
downloaded from the [Oracle Technology Network](http://www.oracle.com/technetwork/oracle-labs/program-languages/downloads/index.html).

Set the environment variable `GRAALVM_DIR` to the location of GraalVM distribution
and run the `build.sh` script from the `fastr_node` directory.
Typical problem is proxy set-up: verify that you have `http_proxy` and `https_proxy`
variables set properly.

####Running application

The application is launched from the `fastr_node` directory
by running the `run.sh` script. Note: as with the installation, the `GRAALVM_DIR`
environment variable must be set to point to the GraalVM distribution.

Open your browser at http://localhost:12837.

