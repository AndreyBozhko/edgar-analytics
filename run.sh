#!/bin/bash

DIR=$(dirname ${BASH_SOURCE})

input1=$DIR/input/log.csv
input2=$DIR/input/inactivity_period.txt
output=$DIR/output/sessionization.txt

javac -d $DIR/bin $DIR/src/edgarAnalytics/*.java

java -cp $DIR/bin edgarAnalytics.Main $input1 $input2 $output
