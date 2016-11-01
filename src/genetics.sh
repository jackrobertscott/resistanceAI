#!/bin/bash
echo  >> statistics/$1.csv
counter=0
bestwinrat=0
var=0
varchamp=0
while [ true ]; do
  ./runif.r
  cat xs.txt >> statistics/$1.csv
  java Run | grep appletread
  (echo winratio >> statistics/$1.csv)
  cat Results.html | grep Tahmer | grep '^<tr><td>' | sed -e  's:<tr>::g' -e  's:</tr>::g' -e  's:</td>::g' -e  's:<td>: :g' | cut -c2- | grep -oE '[^ ]+$' >> statistics/$1.csv
  var=$( cat Results.html | grep Tahmer | grep '^<tr><td>' | sed -e  's:<tr>::g' -e  's:</tr>::g' -e  's:</td>::g' -e  's:<td>: :g' | cut -c2- | grep -oE '[^ ]+$' )
  cat Results.html | grep Erndog | grep '^<tr><td>' | sed -e  's:<tr>::g' -e  's:</tr>::g' -e  's:</td>::g' -e  's:<td>: :g' | cut -c2- | grep -oE '[^ ]+$' >> statistics/$1.csv
  varchamp=$( cat Results.html | grep Erndog | grep '^<tr><td>' | sed -e  's:<tr>::g' -e  's:</tr>::g' -e  's:</td>::g' -e  's:<td>: :g' | cut -c2- | grep -oE '[^ ]+$' )
  if (( $(bc <<< "$var > $bestwinrat") )); then
    cat xs.txt > xsBestwinratio.txt
    bestwinrat=$(echo "$var" | bc)
  fi
  if (( $(bc <<< "$var > $varchamp") )); then
    cat xs.txt > xsBest.txt
  fi
  let counter=counter+1
  echo $counter
done
