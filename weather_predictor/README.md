# Weather Predictor Application

This is a demonstration of GraalVM's polyglot capabilities showing
how objects and functions can be shared among different languages.

The application is a simple REST service and simple web page for predicting temperature in
a given city. The predictor uses a model constructed by applying linear regression
to a data set consisting of temperatures in selected cities expressed as
`(latitude, temperature)` tuples. The temperatures are retrieved in the Ruby part of the application
via a [gem](https://rubygems.org/gems/openweather2/versions/0.1.8) wrapping [OpenWeatherMap API](http://openweathermap.org) service,
while the corresponding latitudes and other cities data are retrieved from Java
class that mocks a typical data access object and can be easily extended to talk
to a real database.

The regression model is also built in the R module by means of the [lm](https://stat.ethz.ch/R-manual/R-devel/library/stats/html/lm.html) function. To predict the temperature
in a given city the R module defines a function passing the city's latitude into the
[predict](https://stat.ethz.ch/R-manual/R-devel/library/stats/html/predict.lm.html)
function, which uses the model to return the predicted temperature for the given latitude.

The predictor service is exposed in the JavaScript module of the application, which is
designed as a simple Express.js based [Node.js](https://nodejs.org/en/) server. This module is logically divided into three stages. First,
the Ruby module is loaded to initialize the OpenWeather service. Then the R module is loaded
to build the prediction model. And finally, the Express.js server is launched.
The server serves the web page UI at http://localhost:12836/ and one can directly
query the REST API at http://localhost:12836/predict/<city>, e.g.
http://localhost:12836/predict/London. The reply to this query is a JSON object carrying
the city's real and predicted temperature, such as
`{"city":"London","real":6.82,"predicted":14.017807794947569}`.

####Installation

In order to run the demonstration, GraalVM 0.25+ must be installed. It can be
downloaded from the [Oracle Technology Network](http://www.oracle.com/technetwork/oracle-labs/program-languages/downloads/index.html).

Set the environment variable `GRAALVM_DIR` to the location of GraalVM distribution
and run the `install.sh` script from the `graal-weather-predictor` directory.
Typical problem is proxy set-up: verify that you have `http_proxy` and `https_proxy`
variables set properly.

####Running application

The application is launched from the `graal-weather-predictor` directory
by running the `run.sh` script. Note: as with the installation, the `GRAALVM_DIR`
environment variable must be set to point to the GraalVM distribution.

Open your browser at http://localhost:12836.
