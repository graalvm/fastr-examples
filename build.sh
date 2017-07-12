#!/bin/bash
set -e

# temporarily disabled until graalvm-0.25
# ./testWeatherPredictor.sh
# ./stop.sh

cd fastr_javaui
./build.sh
cd ..
