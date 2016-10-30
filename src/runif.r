#!/usr/bin/Rscript

r <- runif(15)

write(r, file = "xs.txt")

write(r, file = "./xs.txt")
