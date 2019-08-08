# Mixed Interactive Debugging of R and Native Code with FastR

## Prerequisites

### Visual Studio Code

Visit the Visual Studio Code [download page](https://code.visualstudio.com/download) and download the installer suiting your platform.

### Open Demo Workspace

Add the `fastr_llvm_debug_demo` folder to the workspace via the `File` menu or clicking the Add Folder button in the Explorer. The folder
already contains the debugger configuration.

### VSC R Plugin

Select the `Preferences | Extensions` menu and type `R` in the search box on the top of the `Extensions` panel.
The search result should include the R plugin by Yuki Ueda. Select it and click the `Install` button situated
just below the caption of the plugin's description page.

### FastR

For the sake of simplicity, there is a Docker image having all necessary components preinstalled. Pull the image using the following command.

``` 
docker pull zslajchrt/fastr_llvm_debug_demo
``` 

Then execute the image by

```
docker run -p 9229:9229 -it zslajchrt/fastr_llvm_debug_demo bash
```

and launch FastR by entering `R` from the bash command line.

## Running GraalVM Examples

### Building a simple native library

```
cd /fastr_llvm_debug_demo/simple/
R CMD SHLIB -o lapplyNative.so lapplyNative.c
```

### Using the simple native library in R

Launch FastR with the LLVM backend and with the right options that activate the GraalVM debugger:

```
R --inspect=0.0.0.0:9229 --inspect.Secure=false --R.BackEndLLVM --R.DebugLLVMLibs
```

Then launch Visual Studio Code and press F5 to attach to the GraalVM debugger. Return to FastR
and run the following snippet:

```
dyn.load("/fastr_llvm_debug_demo/simple/lapplyNative.so")
source("lapplyNative.R")
x <- list(a = 1:5, b = rnorm(10))
lapplyNative(x, sum)
```

Switch to VSC and locate `lapplyNative.c` in the Loaded Scripts panel. Then toggle a breakpoint 
there and re-run `lapplyNative(x, sum)`.

### Installing a package utilizing Rcpp

```
cd /fastr_llvm_debug_demo
R CMD INSTALL gibbs
```

### Debugging a package utilizing Rcpp

```
R --inspect=0.0.0.0:9229 --inspect.Secure=false --R.BackEndLLVM --R.DebugLLVMLibs
```

```
library(gibbs)
gibbs_cpp(100, 10)
```

Locate `gibbs.cpp` in VSC's Loaded Scripts panel, toggle a breakpoint there and re-run `gibbs_cpp(100, 10)`.
Debugger should stop at the breakpoint.
