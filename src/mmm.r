#! /usr/bin/env Rscript
d<-scan("stdin", quiet=TRUE)
cat(min(d)/100, max(d)/100, mean(d)/100, sep=",")
cat(sep="\n")
