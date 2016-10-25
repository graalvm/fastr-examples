#!/bin/bash

GEMS_DIR=/Library/Ruby/Gems/2.0.0/gems
OPEN_WEATHER_VERSION=0.1.8
RUBYOPT="-I $GEMS_DIR/openweather2-$OPEN_WEATHER_VERSION/lib"

RUBYOPT=$RUBYOPT $GRAALVM_DIR/bin/node weatherServer.js