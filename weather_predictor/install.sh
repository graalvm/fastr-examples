#!/bin/bash

CRAN_MIRROR=http://cran.us.r-project.org

if [ ! -d openweather ]; then
	echo "Installing openweather gem..."
	# Uncomment after the proxy settings fix is merged into lucasocon repo
	# git clone https://github.com/lucasocon/openweather.git
	git clone https://github.com/zslajchrt/openweather.git
	cd openweather
	git checkout proxyFix
	cd ..
fi

# Install the maps package if not installed yet
if [ ! -d $GRAALVM_DIR/language/R/library/maps ]; then
	echo "Installing maps package..."

	$GRAALVM_DIR/bin/Rscript -e "install.packages('maps', repos='$CRAN_MIRROR')"

	FASTR_LOG=$GRAALVM_DIR/language/R/fastr_errors.log
	if [ -f FASTR_LOG ]; then
		echo "Dumping fastr_errors.log:"
		cat $GRAALVM_DIR/language/R/fastr_errors.log
	fi
fi

