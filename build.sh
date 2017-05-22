#!/bin/bash
set -e

./testWeatherPredictor.sh
./stop.sh

cd fastr_javaui
./build.sh
cd ..
