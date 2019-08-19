# Shared domain logic semi realistic example

This example contains a small web server written in Ruby which shares a domain 
logic written in JS with a JS frontend GUI running in a browser. The web 
application consists of a web frontend with one table of people and an 
interactive form with live validations. 

## Running

`export GRAALVM_HOME=.../graalvm-ee-1.0.0-rc6/` or
`export GRAALVM_HOME=.../graalvm-ee-1.0.0-rc6/Content/Home` on mac.
Then execute `./run.sh` which will install deps. and start the web server.
