#!/bin/bash

# Fail on any error
set -e

# Copy graalvm to the working dir
if [ ! -d graalvm ]; then
	echo "Copying graalvm to working directory..."
	mkdir graalvm
	cp -R $GRAALVM_DIR/* graalvm
fi
# Override the GRAALVM_DIR variable
GRAALVM_DIR=`pwd`/graalvm
export GRAALVM_DIR

cd weather_predictor

./install.sh
./run.sh &

sleep 60
echo "Testing..."

./test.sh

cd ..
