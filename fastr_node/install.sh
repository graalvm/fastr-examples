#!/bin/bash
export NODE_STACK_SIZE=2000000

# Install expressjs
echo "Running npm install"
$GRAALVM_DIR/bin/npm install
