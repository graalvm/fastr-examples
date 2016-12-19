#!/bin/bash

GEM_DIR=openweather/lib
OPEN_WEATHER_VERSION=0.1.8
RUBYOPT="-I $GEM_DIR"

RUBYOPT=$RUBYOPT $GRAALVM_DIR/bin/node weatherServer.js
