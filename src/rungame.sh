#!/bin/bash
echo "players =  $2" >> statistics/$3.csv
wha=0
if [ -n "$1" ]
then
  while [ $wha -lt $1 ]; do
    (
    apples=0
    while [ $apples -lt 10 ]; do
      (
      COUNTER=0
      while [  $COUNTER -lt 100 ]; do
        java cits3001_2016s2.Game $2
        let COUNTER=COUNTER+1
      done
      )  | grep -c Resistance
      let apples=apples+1
    done
    ) > statistics/out.txt
    ./mmm.r < statistics/out.txt >> statistics/$3.csv
    let wha=wha+1
  done
fi
