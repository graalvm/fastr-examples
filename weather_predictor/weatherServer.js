fs = require('fs');

// Load the Ruby module
console.log("Initializing Openweather");
var weatherInitScript = fs.readFileSync("weatherInit.rb", "utf8");
Interop.eval("application/x-ruby", weatherInitScript);

Weather = Interop.import('weather')
Interop.export('tempInCity', function(name) {
	return Weather.temperature_in_city(name);
});

// Load the R module
console.log("Preparing weather model... This may take a while depending on your connection");
var weatherModelScript = fs.readFileSync("weatherModel.r", "utf8");
Interop.eval("application/x-r", weatherModelScript);

// Import the function exported from the R module
createModel = Interop.import('createModel');
predictTemp = Interop.import('do_predict');
plotModel = Interop.import('plotModel');
plotMap = Interop.import('plotMap');
isCity = Interop.import('isCity');

var cityService = new com.oracle.graalvm.demo.weather.CityService();

var updateTemperatures = function() {
    // TODO: implement this function in the ruby module (and optionally make only one batch request to open weather API)
    // The problem is probably that cityService is not wrapped in TruffleObject by Graal.js -- this is fixed in the latest Graal.js
    let cities = cityService.getAll();
    for (var i = 0; i < cities.length; i++) {
        console.log("Updating temperature of " + cities[i].getName());
        cityService.updateTemperature(cities[i].getId(), Weather.temperature_in_city(cities[i].getName()));
    }
}

// Create the linear regression model
var updateModel = function(size) {
    // TODO: implement this function in the R module by passing cities to it and letting it invoke the getName and other methods by itself
    // The problem is that cityService is not wrapped in TruffleObject by Graal.js -- this is fixed in the latest Graal.js
    // Yet another problem is that support for foreign arrays in FastR is implemented only in the latest version
    var cities = cityService.getAll();
    let getName = function(i) { return cities[i-1].getName(); }
    let getLatitude = function(i) { return cities[i-1].getLatitude(); }
    let getLongitude = function(i) { return cities[i-1].getLongitude(); }
    let getTemperature = function(i) { return cities[i-1].getTemperature(); }
    return createModel(size, cities.length, getName, getLatitude, getLongitude, getTemperature);
}

var model = updateModel(5);

// Expressjs application:
var express = require('express');
var app = express();

app.get('/cities', function (req, res) {
    // TODO: use Jackson or nicer built-in JSON serialization story Java objects in JavaScript?
    // use Jackson internally in JavaScript JSON serializer for Java objects?
    // let people choose and plug in any JSON framework?
    let cities = cityService.getAllPaged(req.query.skip, req.query.limit);
    let jsCities = [];
    for(let i = 0; i < cities.length; ++i) {
        jsCities.push({
            name: cities[i].getName(),
            country: cities[i].getCountry(),
            population: cities[i].getPopulation(),
            latitude: cities[i].getLatitude(),
            longitude: cities[i].getLongitude(),
            temperature: cities[i].getTemperature()
        });
    }
    res.send({data = jsCities, totalCount = cityService.getTotalCount()});
});

app.get('/predict/:city', function (req, res) {
    var city = cityService.findByName(req.params.city);
    if (city) {
        res.send({predicted: predictTemp(model, city.getLatitude()), real: Weather.temperature_in_city(req.params.city)});
    } else {
        res.status(404).send('City not found');
    }
});

app.post('/regenerate/:size', function(req, res) {
    model = updateModel(req.params.size);
    res.status(200).send('');
});

app.post('/update-tempratures', function(req, res) {
    updateTemperatures();
    res.status(200).send('');
});

app.get('/model-plot', (req, res) => res.send(plotModel(model)));
app.get('/model-map', (req, res) => res.send(plotMap(model)));

app.use(express.static(__dirname + "/public"));
app.listen(12836, function() {
    console.log("Server listening on port 12836");
});
