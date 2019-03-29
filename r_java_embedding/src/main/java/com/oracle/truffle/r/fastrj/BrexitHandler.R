require(jsonlite)
require(dplyr)
require(ggplot2)

# Plots a simple bar chart of the brexit petition data and returns the SVG representation of the plot.	
svgExample1 <- function(model) { 
	svg()
    print(ggplot(model, aes(name, signature_count, fill = signature_count)) + geom_col())
	grDevices:::svg.off() 
}

brexit <- fromJSON('https://petition.parliament.uk/petitions/241584.json')
brexitByConstituency <-	brexit$data$attributes$signatures_by_constituency %>% arrange(desc(signature_count))
