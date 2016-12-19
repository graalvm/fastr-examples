#!/bin/bash

cd weather_predictor

./install.sh
./patch.sh
./run.sh &

sleep 30
echo "Testing..."

./test.sh

PID=`ps x | grep nodeo | grep graal | awk '{print $1}'`
echo "Killing server (PID=$PID)"
kill $PID
cd ..