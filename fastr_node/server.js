fs = require('fs');

// Load the R module
var modelScript = fs.readFileSync("model.r", "utf8");
Interop.eval("application/x-r", modelScript);

// Import the function exported from the R module
plotKMeans = Interop.import('plotkmeans');
plotCars = Interop.import('plotcars');
plotWeight = Interop.import('plotheightweight');
predictWeight = Interop.import('predictweight');

// Expressjs application:
var express = require('express');
var app = express();

app.get('/kmeans', function (req, res) {
    console.log('generating kmeans graph');
    res.send(plotKMeans(req.query.xaxis, req.query.yaxis, req.query.clusters));
});

app.get('/cars', function (req, res) {
    console.log('generating cars graph');
    res.send(plotCars(req.query.xaxis, req.query.yaxis, req.query.zaxis));
});

app.get('/lm', function (req, res) {
    console.log('generating height/weight plot');
    res.send(plotWeight());
});

app.get('/lm/predict', function (req, res) {
    console.log('predicting weight');
    res.send('' + predictWeight(parseInt(req.query.height, 10)));
});

app.use(express.static(__dirname + "/public"));
app.listen(12836, function() {
    console.log("Server listening on port 12836");
});
