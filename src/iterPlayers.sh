#!/bin/bash
players=9
echo "percentage of times the Resistance Win" >> out.csv
echo "each row is the average of 10 rounds of 100 games" >> out.csv
echo "min,max,avg" >> out.csv
while [ $players -lt 10 ]; do
  ./rungame.sh 1 $players
  let players=players+1
done
