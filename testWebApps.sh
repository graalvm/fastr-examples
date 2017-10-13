#!/bin/bash

# Fail on any error
set -e

function waitForServer {
    while read line; do
        if grep -q 'Server listening' <<< $line; then
            break;
        fi
    done <&3
}

# ------------------
# Weather preditor

cd weather_predictor

./install.sh
exec 3< <(./run.sh)
waitForServer

echo "Testing Weather Predictor..."
./test.sh
../stop.sh

cd ..

# -------------------
# FastR Node

cd fastr_node

./install.sh
exec 3< <(./run.sh)
waitForServer

echo "Testing FastR Node.js example..."
./test.sh
../stop.sh

cd ..
