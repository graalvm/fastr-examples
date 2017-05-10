#!/bin/bash

CRAN_MIRROR=http://cran.us.r-project.org

if [ ! -d openweather ]; then
	echo "Installing openweather gem..."
	git clone https://github.com/lucasocon/openweather.git
	cd openweather
	git checkout d5f49d3c567bd1ac3e055a65189661d8d3851c7f
	cd ..
fi

# Install the maps and lattice packages if not installed yet
if [ ! -d $GRAALVM_DIR/language/R/library/maps ]; then
	echo "Installing maps package..."

	$GRAALVM_DIR/bin/Rscript -e "install.packages(c('maps', 'lattice'), repos='$CRAN_MIRROR')"

	FASTR_LOG=$GRAALVM_DIR/language/R/fastr_errors.log
	if [ -f FASTR_LOG ]; then
		echo "Dumping fastr_errors.log:"
		cat $GRAALVM_DIR/language/R/fastr_errors.log
	fi
fi

# Install expressjs
echo "Running npm install"
$GRAALVM_DIR/bin/npm install
