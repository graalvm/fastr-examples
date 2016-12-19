# Import the tempInCity function exported from the Ruby module
tempInCity <- .fastr.interop.import('tempInCity')

# The maps library is needed to determine the latitudes for cities
library(maps)

# Create the learning set of cities used in the model
cities <- world.cities[1:10*3100,c("name","lat")]

# Add temperatures obtained from tempInCity
cities$temperature <- sapply(cities$name, function(x) tempInCity(x))

# Build the model
model <- lm(temperature~lat, data=cities)

# An auxiliary function for getting the latitude for a given city
latitude <- function(city) { 
	res <- world.cities[match(city, world.cities[[1]]), c("name","lat")]
	res$lat[1] 
}

# The predictor function. It returns the predicted temperature for the given city
predictTemp <- function (city) { 
	latit <- latitude(city)
	m <- data.frame(name=city, lat=latit)
	res <- predict(model, newdata=m)
	res[1]
}
	
# An auxiliary function returning the current (i.e. not predicted) temperature
# using OpenWeather
realTemp <- function(city) {
	tempInCity(city)
}

# Export the functions
.fastr.interop.export('predictTemp', predictTemp)
.fastr.interop.export('realTemp', realTemp)