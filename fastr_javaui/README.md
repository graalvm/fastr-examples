# R's graphical output in Java based desktop application

This demo shows an interactive Java based desktop application that displays k-means 
clustering on the iris data set. The user can choose variables for the x and y axes 
and the number of clusters. The UI is updated accordingly in real time. The clustering 
and plotting is done in R using `kmeans` function from the stats package and `xyplot` 
function from the lattice package. 

Under the hood, FastR emulates the grid and grDevices packages purely in Java and 
is capable of drawing directly to Graphics2D context without the typical cost of 
crossing language boundaries. 


#### Building and running the example

Once you have GraalVM installed, it is necessary to install the lattice R package. 
Start interactive FastR session using 

```
$GRAAL_VM/bin/R
```

and type:

```
install.packages('lattice')
```

Now you can build and run this example using the `build.sh` and `run.sh` scripts.
