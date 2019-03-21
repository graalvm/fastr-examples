require(dplyr)
require(ggplot2)
require(kableExtra)

# A sample query using dplyr and kable to generate a HTML table from the iris dataset.
# Parameters:
# 	_data_:
# 	_len_:
kableQuerySample1 <- quote(_data_ %>% 
	mutate_if(is.numeric, function(x) { 
				cell_spec(x, bold = T, 
					color = spec_color(x, end = 0.9), 
					font_size = spec_font_size(x)) 
	}) %>% 
	mutate(Species = cell_spec(Species, 
    			color = 'white', bold = T, 
			background = spec_color(1:_len_, end = 0.9, option = 'A', direction = -1))
	) %>% 
	kable(escape = F, align = 'c') %>% 
	kable_styling(c('striped', 'condensed'), full_width = F))
