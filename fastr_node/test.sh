#!/bin/bash
echo Testing GraalVM FastR Node.js interop example running at localhost:12837
echo getting index.html
if ! curl -s --head --noproxy localhost --request GET 'http://localhost:12837/index.html' | grep "200 OK" > /dev/null; then
  exit 1
fi
echo getting kmeans visualization
if ! curl -s --noproxy localhost --request GET 'http://localhost:12837/kmeans?xaxis=Sepal.Length&yaxis=Sepal.Width&clusters=4' | grep "<svg" > /dev/null; then
  exit 2
fi
echo getting cars cloud plot
if ! curl -s --noproxy localhost --request GET 'http://localhost:12837/cars?xaxis=mpg&yaxis=cyl&zaxis=disp' | grep "<svg" > /dev/null; then
  exit 3
fi
echo getting linear regression visualization
if ! curl -s --noproxy localhost --request GET 'http://localhost:12837/lm' | grep "<svg" > /dev/null; then
  exit 4
fi
echo getting linear regression prediction
if ! curl -s --noproxy localhost --request GET 'http://localhost:12837/lm/predict/?height=80' | grep "188" > /dev/null; then
  exit 5
fi
echo DONE
