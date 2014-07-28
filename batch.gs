#!/bin/gosl

Methods := []string {
  "nopool", "sync", "lockfree",
}

MaxThreads := 10

ElSizes := []int {
  1, 10, 100, 1000, 10000,
}

var results [3][10][5]int

for iMth, mth := range Methods {
  for nThread := 1; nThread <= MaxThreads; nThread++ {
    for iElSize, elSize := range ElSizes {
      Printfln("Tesing: method=%s, nThread: %d, ElSize: %d", mth, nThread, elSize)
      out := BashEval("java Benchmark %s 1000 %d %d | grep Total", mth, nThread, elSize)
      out = TrimSpace(out[7:])
      Println(out)
      results[iMth][nThread-1][iElSize] = I(out)
    }
  }
}

Printfln("Method,Threads,ElSize,Ms")
for iMth, mth := range Methods {
  for nThread := 1; nThread <= MaxThreads; nThread++ {
    for iElSize, elSize := range ElSizes {
      Printfln("%v,%v,%v,%v", mth, nThread, elSize, results[iMth][nThread-1][iElSize])
    }
  }
}

