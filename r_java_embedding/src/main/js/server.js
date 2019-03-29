console.log("Initializing FastRJ server");

var express = require('express');
var app = express();
var BrexitHandler = Java.type('com.oracle.truffle.r.fastrj.BrexitHandler');

app.get('/brexit', function (req, res) {
	var handler = BrexitHandler.getHandler(req);
	res.contentType('image/svg+xml');
    res.send(handler.plotChart(parseInt(req.query.maxCols)));
});

var IrisHandler = Java.type('com.oracle.truffle.r.fastrj.IrisHandler');
app.get('/iris', function (req, res) {
	var handler = IrisHandler.getHandler(req);
	res.contentType('text/html');
    res.send("<html>" + handler.generateTable(parseInt(req.query.maxRows)) + "</html>");
});

var port = 12836;
var server = app.listen(port, function() {
    console.log("Server listening on http://localhost:" + port);
});

