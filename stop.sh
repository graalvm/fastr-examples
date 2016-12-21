#!/bin/bash

PID=`ps x | grep nodeo | grep graal | awk '{print $1}'`
echo "Stopping server (PID=$PID)"
kill $PID