#!/bin/bash
echo Testing GraalVM weather predictor example running at localhost:12836
echo getting index.html
if ! curl -s --head --noproxy localhost --request GET http://localhost:12836/index.html | grep "200 OK" > /dev/null; then
  exit 1
fi
echo getting prediction for Kladno
if ! curl -s --head --noproxy localhost --request GET http://localhost:12836/predict/Kladno | grep "200 OK" > /dev/null; then
  exit 2
fi
echo getting svg visualization
if ! curl -s --noproxy localhost --request GET http://localhost:12836/model-plot | grep "<svg" > /dev/null; then
  exit 3
fi
echo DONE
