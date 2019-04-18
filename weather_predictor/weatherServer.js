/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This file is made available under version 3 of the GNU General Public License.
 */

fs = require('fs');

// Load the Ruby module
console.log("Initializing Openweather");
var weatherInitScript = fs.readFileSync(__dirname + "/weatherInit.rb", "utf8");
Polyglot.eval("application/x-ruby", weatherInitScript);

// Ruby overrides Node.js signal handler, we override it back
process.on('SIGINT', function() { process.exit(0); });

Weather = Polyglot.import('weather')
Polyglot.export('tempInCity', function(name) {
	return Weather.temperature_in_city(name);
});

// Load the R module
console.log("Preparing weather model...");
var weatherModelScript = fs.readFileSync(__dirname + "/weatherModel.r", "utf8");
Polyglot.eval("application/x-r", weatherModelScript);

// Import the function exported from the R module
createModel = Polyglot.import('createModel');
predictTemp = Polyglot.import('do_predict');
plotModel = Polyglot.import('plotModel');

const cityServiceType = Java.type('com.oracle.graalvm.demo.weather.CityService');
var cityService = new cityServiceType();

var updateTemperatures = function() {
    let cities = cityService.getAll();
    for (var i = 0; i < cities.length; i++) {
        console.log("Updating temperature of " + cities[i].getName());
        cityService.updateTemperature(cities[i].getId(), Weather.temperature_in_city(cities[i].getName()));
    }
}

// Create the linear regression model
var updateModel = function(size) {
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
    let cities = cityService.getAllPaged(parseInt(req.query.skip), parseInt(req.query.limit));
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
    res.send({data : jsCities, totalCount : cityService.getTotalCount()});
});

app.get('/predict/:city', function (req, res) {
    var city = cityService.findByName(req.params.city);
    if (city) {
        res.send({predicted: predictTemp(model, city.getLatitude()), real: city.getTemperature(), loaded: Weather.temperature_in_city(city.getName())});
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

var port = 12836;
app.use(express.static(__dirname + "/public"));
var server = app.listen(port, function() {
    console.log("Server listening on http://localhost:" + port);
});

app.get('/exit', function(req, res) {
    res.status(200).send('');
    server.close();
});
