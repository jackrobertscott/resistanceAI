#!/bin/bash
wha=0
if [ -n "$1" ]
then
  while [ $wha -lt $1 ]; do
    (
    apples=0
    while [ $apples -lt 5 ]; do
      (
      COUNTER=0
      while [  $COUNTER -lt 100 ]; do
        java cits3001_2016s2.Game
        let COUNTER=COUNTER+1
      done
      )  | grep -c Resistance
      let apples=apples+1
    done
    ) > out.txt
    ./mmm.r < out.txt >> out.csv
    let wha=wha+1
  done
fi
