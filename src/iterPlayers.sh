#!/bin/bash
players=5
echo "percentage of times the Resistance Win" > $1.csv
echo "each row is the average of 10 rounds of 100 games" >> $1.csv
echo "min,max,avg" >> $1.csv
while [ $players -lt 11 ]; do
  ./rungame.sh 1 $players $1
  let players=players+1
done
rm out.txt
