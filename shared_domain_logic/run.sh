#!/bin/bash

set -e

# Resolve the location of this script
source="${BASH_SOURCE[0]}"
while [ -h "$source" ]; do
  prev_source="$source"
  source="$(readlink "$source")"
  if [[ "$source" != /* ]]; then
    # if the link was relative, it was relative to where it came from
    dir="$(cd -P "$(dirname "$prev_source")" && pwd)"
    source="$dir/$source"
  fi
done
dir="$(cd -P "$(dirname "$source")" && pwd)"

: "${GRAALVM_DIR?"GRAALVM_DIR must point to a GraalVM image"}"

echo "Go to localhost:4567"
echo "Quit the application by following the link at the bottom of the page"
echo

set -x

"${GRAALVM_DIR}/jre/languages/ruby/bin/ruby" --jvm --polyglot --experimental-options --ruby.single-threaded=true "${dir}/app.rb"
