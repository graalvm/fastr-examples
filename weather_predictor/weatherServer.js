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
isCity = Interop.import('isCity');

// Create the linear regression model
var model = createModel(10)
console.log("...done")

// Expressjs application:
var express = require('express');
var app = express();

app.get('/predict/:city', function (req, res) {
	if (isCity(req.params.city)) {
	  res.send({predicted: predictTemp(model, req.params.city), real: Weather.temperature_in_city(req.params.city)});
  } else {
		res.status(404).send('City not found');
	}
});

app.get('/model-plot', function (req, res) {
	res.send(plotModel(model));
});

app.use(express.static(__dirname + "/public"));
app.listen(12836, function() {
	console.log("Server listening on port 12836");
});
