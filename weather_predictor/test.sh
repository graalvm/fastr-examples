#!/bin/bash
# Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
# This file is made available under version 3 of the GNU General Public License.

set -e
set -o xtrace

# Resolve the location of this script
source="${BASH_SOURCE[0]}"
while [ -h "$source" ] ; do
  prev_source="$source"
  source="$(readlink "$source")";
  if [[ "$source" != /* ]]; then
    # if the link was relative, it was relative to where it came from
    dir="$( cd -P "$( dirname "$prev_source" )" && pwd )"
    source="$dir/$source"
  fi
done
dir="$( cd -P "$( dirname "$source" )" && pwd )"

: ${GRAALVM_DIR?"GRAALVM_DIR must point to a GraalVM image"}

exec 3< <(${GRAALVM_DIR}/bin/node ${GRAALVM_ADDITIONAL_ARGS} --jvm --vm.classpath=${dir}/bin --ruby.load-paths=${dir}/openweather/lib,${dir} --polyglot ${dir}/weatherServer.js)

while read line; do
  if grep -q 'Server listening' <<< $line; then
    break
  fi
done <&3

port=12836

function stop() {
  curl -s --noproxy localhost --request GET http://localhost:${port}/exit >/dev/null
}

# $1 message
# $2 path to GET
# $3 validation string
function test() {
  echo "$1"
  response=`curl -s --include --noproxy localhost --request GET http://localhost:${port}/$2 2>&1`
  if ! echo "$response" | grep "$3" 2>&1 >/dev/null; then
    echo "ERROR"
    echo "$response"
    stop
    exit 1
  fi
}

echo -e "Testing GraalVM weather predictor example running at http://localhost:${port}\n"

test "getting index.html" "index.html" "200 OK"
test "getting prediction for London" "predict/London" "200 OK"
test "getting svg visualization" "model-plot" "<svg"
test "terminating server" "exit" "200 OK"

echo -e "\nDONE"
