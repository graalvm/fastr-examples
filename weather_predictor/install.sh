#!/bin/bash

CRAN_MIRROR=http://cran.us.r-project.org

if [ ! -d openweather ]; then
	# Uncomment after the proxy settings fix is merged into lucasocon repo
	# git clone https://github.com/lucasocon/openweather.git
	git clone https://github.com/zslajchrt/openweather.git
	cd openweather
	git checkout proxyFix
	cd ..
fi

$GRAALVM_DIR/bin/Rscript -e "install.packages('maps', repos='$CRAN_MIRROR')"

