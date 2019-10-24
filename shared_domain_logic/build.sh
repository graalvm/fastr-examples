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

set -x

: "${GRAALVM_DIR?"GRAALVM_DIR must point to a GraalVM image"}"

"${GRAALVM_DIR}/bin/gem" install sinatra --version 2.0.5 --no-document
