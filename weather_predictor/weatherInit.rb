require 'openweather2'

# Initialize the OpenWeather service. A personal API key must be substituted for <<API-KEY>>.
Openweather2.configure do |config| 
	config.endpoint = 'http://api.openweathermap.org/data/2.5/weather'
	config.apikey = '<<API-KEY>>'
end

# Return the current temperature in the given city
def tempInCity(name) 
	weather = Openweather2.get_weather(city: Truffle::Interop.from_java_string(name), units: 'metric')
	weather.temperature; 
end

# Export the tempInCity function so that it can be used in other languages
Truffle::Interop.export('tempInCity', method(:tempInCity))