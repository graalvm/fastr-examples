# Shared domain logic semi realistic example

This example contains a small web server written in Ruby which shares a domain 
logic written in JS with a JS frontend GUI running in a browser. The web 
application consists of a web frontend with one table of people and an 
interactive form with live validations. 

## Building & Running

`export GRAALVM_DIR=.../graalvm-VERSION/` or
`export GRAALVM_DIR=.../graalvm-VERSION/Content/Home` on mac.

Then execute `./build.sh` to install dependencies (not necessary if you ran
`../build_all.sh` beforehand) and `./run.sh` to start the web server.
