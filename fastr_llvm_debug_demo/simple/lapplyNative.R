populateIntVector <- function(n) {
    .Call("populateIntVector", as.integer(n))
}

lapplyNative <- function (x, fun, env = new.env()) {
    .Call("lapplyNative", x, fun, env)
}
