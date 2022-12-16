#!/bin/bash

FILENAME=results.csv
FILES="instances/*.csp"

for file in $FILES;
do
    echo "$file fc asc asc" >> $FILENAME 
    java -jar CS4402-Solver.jar $file fc asc asc >> $FILENAME 
    echo "$file fc sdf asc" >> $FILENAME 
    java -jar CS4402-Solver.jar $file fc asc asc >> $FILENAME
    echo "$file mac asc asc" >> $FILENAME 
    java -jar CS4402-Solver.jar $file fc asc asc >> $FILENAME 
    echo "$file mac sdf asc" >> $FILENAME 
    java -jar CS4402-Solver.jar $file fc asc asc >> $FILENAME 
done