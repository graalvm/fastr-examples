# Weather Predictor Application

This is a demonstration of polyglot capabilities of GraalVM showing
how objects and functions can be shared among different languages.

The resulting applications is a simple REST service predicting temperature in
a given city. The predictor uses a model constructed by applying linear regression
to a data set consisting of temperatures in selected cities expressed as couples 
`(latitude, temperature)`. The temperatures are retrieved in the Ruby part of the application
via a [gem](https://rubygems.org/gems/openweather2/versions/0.1.8) wrapping [OpenWeatherMap API](http://openweathermap.org) service,
while the corresponding latitudes are determined in the R module through the [maps](https://cran.r-project.org/web/packages/maps/maps.pdf) package.

The regression model is also built in the R module by means of the [lm](https://stat.ethz.ch/R-manual/R-devel/library/stats/html/lm.html) function. To predict the temperature
in a given city the R module defines a function passing the city's latitude into the 
[predict](https://stat.ethz.ch/R-manual/R-devel/library/stats/html/predict.lm.html) 
function, which uses the model to return the predicted temperature for the given latitude.

The predictor service is exposed in the JavaScript module of the application, which is
designed as a simple [Node.js](https://nodejs.org/en/) server. This module is logically divided into three stages. First,
the Ruby module is loaded to initialize the OpenWeather service. Then the R module is loaded
to build the prediction model. And finally, the server is launched to accept queries in
the form `http://localhost:8080/<city>`. The reply to this query is a JSON object carrying
the city's name, the real and the predicted temperature, such as
`{"city":"Kladno","real":6.82,"predicted":14.017807794947569}`.

####Installing GraalVM

In order to run the demonstration, the latest GraalVM must be installed. It can be
downloaded from the [Oracle Technology Network](http://www.oracle.com/technetwork/oracle-labs/program-languages/downloads/index.html).

####Installing OpenWeather gem

The OpenWeather gem can be installed as follows:

```
sudo gem install openweather2
```

The Ruby interpreter in GraalVM must be given the path to the gem library directory so that
it can load the OpenWeather gem. The following command reveals the gems installation
directory in the `INSTALLATION DIRECTORY` property:

```
gem environment
```

The path to the OpenWeather gem is specified in the `-I` option as a part of the `RUBYOPT` environment
variable when running the application. The path is constructed as a concatenation of the
gems installation directory and `/gems/openweather2-<version>/lib`.

####Generating a personal key for using OpenWeatherMap API

In order to use the OpenWeatherMap service, a personal key must be obtained first from `openweathermap.org`.
The instructions on how to obtain a key are [here](http://openweathermap.org/appid).

Then, the generated personal key must be substituted for `<<API-KEY>>` in the Ruby module.

####Installing the maps package in FastR

The `maps` package is used in the R module to retrieve latitudes of cities. This package
can be installed from the FastR shell, which can be launched by the following command:

```
graalvm/bin/R
```

In the shell, the following command will install the `maps` package:

```
install.packages("maps")
```

####Running application

The application is launched from the `graal-weather-predictor` directory as follows:

```
cd graal-weather-predictor
RUBYOPT='-I /Library/Ruby/Gems/2.0.0/gems/openweather2-0.1.8/lib' ../graalvm-0.18/bin/node weatherServer.js
```

Once the server is running, a query can be issued as shown below:

```
curl http://localhost:8080/Kladno
```

The reply should look like this:

```
{"city":"Kladno","real":6.82,"predicted":14.017807794947569}
```
