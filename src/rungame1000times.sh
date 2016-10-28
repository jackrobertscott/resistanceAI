#!/bin/bash
COUNTER=0
while [  $COUNTER -lt 100 ]; do
  java cits3001_2016s2.Game | grep -c Resistance
  let COUNTER=COUNTER+1
done
