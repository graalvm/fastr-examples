require(dplyr)

mtcarsCylQ <- quote(mtcars %>% filter(mpg > _mpg_) %>% group_by(cyl) %>% summarise(total = n()))

