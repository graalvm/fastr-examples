#!/bin/bash

CRAN_MIRROR=http://cran.us.r-project.org

yum install rubygems
gem install openweather2
echo "Gem installation directory:"
gem environment | grep "INSTALLATION DIRECTORY"

$GRAALVM_DIR/bin/Rscript -e "install.packages('maps', repos='$CRAN_MIRROR')"