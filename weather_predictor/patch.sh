#!/bin/bash

# Patching weatherInit.rb with OPEN_WEATHER_API_KEY
if [ -n $OPEN_WEATHER_API_KEY ]
then
	echo "Applying OpenWeather API key $OPEN_WEATHER_API_KEY"
	sed -i -- "s/<<API-KEY>>/$OPEN_WEATHER_API_KEY/g" weatherInit.rb
else
	echo "No OpenWeather API key available. You must specify it manually in weatherInit.rb."
fi
