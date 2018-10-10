# Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
# This file is made available under version 3 of the GNU General Public License.

# Compile the Java class that we will use in the benchmark
# Note: you need to have $JAVA_HOME variable defined in the environment
source("java.R")

# rJava initialization
library(rJava)
.jinit()
.jaddClassPath(getwd())

# the object that we are going to use for the benchmark
testObject <- .jnew("RJavaBench")

benchmark <- function(obj) {
    result <- 0L
    for (j in 1:100) {
       obj2 <- obj$objectFunction(obj)
       obj$intField <- as.integer(obj2$doubleField)
       for (i in 1:250) {
           result <- obj$intFunction(i, obj$intField)
       }
    }
    result
}

result <- 0L
timings <- numeric(5)
for (i in 1:10) {
  cat("iteration ", i, "\n")
  time <- system.time(r <- benchmark(testObject))
  print(time)

  # we do some artificial computation with the result so that
  # the compiler cannot possibly ignore the call to benchmark() altogether
  result <- result + if (r > 0) 1 else -1;
  # save the timing
  timings[[i]] <- time[[3]]
}

cat("The computed number is ", result, "\n")
cat("The times are: ")
dput(round(timings, 3))

# cleanup
unlink("RJavaBench.java")
unlink("RJavaBench.class")
