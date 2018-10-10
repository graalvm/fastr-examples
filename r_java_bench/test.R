# Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
# This file is made available under version 3 of the GNU General Public License.

# Tests the examples

# Compile the Java class that we will use in the benchmark
# Note: you need to have $JAVA_HOME variable defined in the environment
source("java.R")

# rJava
library(rJava)
.jinit()
.jaddClassPath(getwd())

obj <- .jnew("RJavaBench")
obj2 <- obj$objectFunction(obj)
obj$intField <- as.integer(obj2$doubleField)
for (i in 1:10) {
   result <- obj$intFunction(i, obj$intField)
}

stopifnot(result == 10)

# Interop
obj <- new("RJavaBench")
obj2 <- obj$objectFunction(obj)
obj$intField <- as.integer(obj2$doubleField)
for (i in 1:10) {
   result <- obj$intFunction(i, obj$intField)
}

stopifnot(result == 10)

# cleanup
unlink("RJavaBench.java")
unlink("RJavaBench.class")
