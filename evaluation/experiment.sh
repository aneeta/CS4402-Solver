#!/bin/bash

FILENAME=res1.csv
FILES="instances/*.csp"

ALGS=('fc' 'mac')
VAR_HEUR=('asc' 'sdf')

echo "instance,algorithm,variable_heuristic,time,nodes,arc_revisions,solution" > $FILENAME

for file in $FILES;
do
    for alg in "${ALGS[@]}";
    do
        for heur in "${VAR_HEUR[@]}";
        do
            start=`date +%s.%N`
            output=$(java -jar CS4402-Solver.jar $file $alg $heur asc | sed -z 's/\n/ /g' | awk '{$1=$1","; $2=$2","; print}' | sed  -z 's/, /,/g')
            end=`date +%s.%N`
            runtime=$( echo "$end - $start" | bc -l ) # timed in seconds.nanoseconds
            echo "$file,$alg,$heur,$runtime,$output" >> $FILENAME
        done
    done
done