fs = require('fs');

// Load the Ruby module
console.log("Initializing Openweather");
var weatherInitScript = fs.readFileSync("weatherInit.rb", "utf8");
Interop.eval("application/x-ruby", weatherInitScript);

// Load the R module
console.log("Preparing weather model");
var weatherModelScript = fs.readFileSync("weatherModel.r", "utf8");
Interop.eval("application/x-r", weatherModelScript);

// Import the function exported from the R module
predictTemp = Interop.import('predictTemp');
realTemp = Interop.import('realTemp');

// A helper to convert the temperatures to a JSON object
function jsonify(cityName, realTmp, predictedTmp) {
	return JSON.stringify({
		city: cityName, 
		real: realTmp, 
		predicted: predictedTmp
	});
}

// Launch the server
console.log("Starting server...");
var http = require("http");
var server = http.createServer(function (inp, out) {
	var cityName = inp.url.substring(1);
	out.end(jsonify(cityName, realTemp(cityName), predictTemp(cityName)));
});
server.listen(12836);
