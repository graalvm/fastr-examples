# Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
# This file is made available under version 3 of the GNU General Public License.

# Compiles the following Java source
# Note: you need to have $JAVA_HOME variable defined in the environment

javaSource <- "
public class RJavaBench {

  public int intField;
  public double doubleField;
  
  public int intFunction(int a, int b) {
    return a - b;
  }
  
  public RJavaBench objectFunction(RJavaBench a) {
    RJavaBench result = new RJavaBench();
    result.intField = (intField + a.intField + 1) / 2;
    result.doubleField = (doubleField + a.doubleField) / 2;
    return result;
  }
}
"

cat("Compiling the Java source code....")
writeLines(javaSource, "RJavaBench.java")
system("$JAVA_HOME/bin/javac RJavaBench.java")
cat("DONE\n")
