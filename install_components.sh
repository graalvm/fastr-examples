#!/bin/bash
set -e

$GRAALVM_DIR/bin/gu install -A nodejs
$GRAALVM_DIR/bin/gu install -A ruby
$GRAALVM_DIR/bin/gu install -A R
