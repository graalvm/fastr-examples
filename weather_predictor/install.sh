#!/bin/bash

CRAN_MIRROR=http://cran.us.r-project.org
export NODE_STACK_SIZE=2000000

if [ ! -d openweather ]; then
	echo "Installing openweather gem..."
	git clone https://github.com/lucasocon/openweather.git
	cd openweather
	git checkout d5f49d3c567bd1ac3e055a65189661d8d3851c7f
	cd ..
fi

# Install expressjs
echo "Running npm install"
$GRAALVM_DIR/bin/npm install

echo "Builing the Java sources"
$(dirname "$0")/build.sh
