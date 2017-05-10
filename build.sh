#!/bin/bash
set -e
./testWeatherPredictor.sh
./stop.sh
./fastr_javaui/build.sh
