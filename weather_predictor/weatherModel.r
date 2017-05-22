# Import the tempInCity function exported from the Ruby module
tempInCity <- .fastr.interop.import('tempInCity')

# The maps library is needed to determine the latitudes for cities
library(maps)
# The lattice library is needed for the visualization
library(lattice)

createModel <- function(size) {
  data <- world.cities[1:size*211,c('name', 'lat')]
  data$temp <- sapply(data$name, function(x) tempInCity(x))
  list(data=data, model=lm(temp~lat, data=data))
}

do_predict <- function(model, city) {
  lat <- world.cities[world.cities$name == city, c('name', 'lat')]
  predict(model$model, lat)[[1]]
}

plotModel <- function(model) {
  svg()
  print(xyplot(temp ~ lat, data = model$data,
    panel = function(x, y) {
      panel.xyplot(x, y, cex=2, pch=19)
      panel.abline(model$model)
  }));
  grDevices:::svg.off()
}

# Export the functions
.fastr.interop.export('createModel', createModel)
.fastr.interop.export('do_predict', do_predict)
.fastr.interop.export('plotModel', plotModel)
.fastr.interop.export('isCity', function(name) any(world.cities$name == name))
