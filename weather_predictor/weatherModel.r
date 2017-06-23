# Import the tempInCity function exported from the Ruby module
tempInCity <- .fastr.interop.import('tempInCity')

# The lattice library is needed for the visualization
library(lattice)

createModel <- function(size, length, getName, getLat, getLong, getTemp) {
  # TODO: ideally this would just get "cities" array and invoke #getName(), etc. by itself, still using sapply
  idx <- sample(1:length, size)
  data <- as.data.frame(list(
      name = sapply(idx, function(i) getName(i)),
      lat = sapply(idx, function(i) getLat(i)),
      long = sapply(idx, function(i) getLong(i)),
      temp = sapply(idx, function(i) getTemp(i))))
  list(data=data, model=lm(temp~lat, data=data))
}

do_predict <- function(model, lat) {
  predict(model$model, as.data.frame(list(lat = lat)))[[1]]
}

plotModel <- function(model) {
  svg()
  print(xyplot(temp ~ lat, data = model$data,
    panel = function(x, y) {
      panel.xyplot(x, y, cex=2, pch=19)
      panel.abline(model$model)
      labelsIdx <- seq(1, length(x), length.out = 10) # show only 10 labels, to make the graph more readable
      panel.text(x[labelsIdx] + 1, y[labelsIdx], model$data$name[labelsIdx], adj = c(0, 0.5))
  }));
  grDevices:::svg.off()
}

# Map visualization: comment all the code out and leave "not available yet" if you do not want/cannot install the R libraries below
# This requires the latest FASTR
# The maps library is needed to determine the latitudes for cities
# library(maps)
# ggplot2 is needed for map visualization
# library(ggplot2)

# shows the cities on world map as points coloured from blue to red depending on the temperature
plotMap <- function(model) {
    # svg()
    # print(ggplot(model$data, aes(long, lat)) +
    #     borders("world2") +
    #     geom_point(aes(color = temp)) +
    #     scale_size_area() +
    #     coord_quickmap() +
    #     scale_colour_gradient(low = "#0000aa", high="#ff0000"))
    # grDevices:::svg.off()
    "not available yet"
}



# Export the functions
.fastr.interop.export('createModel', createModel)
.fastr.interop.export('do_predict', do_predict)
.fastr.interop.export('plotModel', plotModel)
.fastr.interop.export('plotMap', plotMap)
