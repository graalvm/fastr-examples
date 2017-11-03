#!/bin/bash

set -e

: ${GRAALVM_DIR?"GRAALVM_DIR must point to a GraalVM image"}

exec ${GRAALVM_DIR}/bin/Rscript $(dirname $0)/gameoflife.R
