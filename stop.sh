#!/bin/bash

PID=`ps x | grep node | grep graal | awk '{print $1}'`
echo "Stopping server (PID=$PID)"
kill $PID
