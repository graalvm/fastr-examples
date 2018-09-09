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

set -x

"${dir}/run.sh" &

url="http://localhost:4567"
set +x
while ! curl -s "$url/people.json"; do
  echo -n .
  sleep 1
done
set -x

test '[{"firstName":"John","lastName":"Doe","age":50}]' = "$(curl -s "$url/people.json")"

curl \
  --header "Content-Type: application/json" \
  --request POST \
  --data '{"firstName":"Jan","lastName":"Novak","age":40}' \
  http://localhost:4567/person.json

test '[{"firstName":"John","lastName":"Doe","age":50},{"firstName":"Jan","lastName":"Novak","age":40}]' = "$(curl -s "$url/people.json")"

curl "$url/exit"
