#!/bin/gosl

Methods := [...]string {
  "nopool", "sync", "lockfree",
}

Threads := [...]int {
  1, 2, 4, 8, 16, 32,
}

ElSizes := [...]int {
  1, 10, 100, 1000, 10000,
}

var results[len(Methods)][len(Threads)][len(ElSizes)]int

for iElSize, elSize := range ElSizes {
  for iMth, mth := range Methods {
    for iThread, nThread := range Threads {
      Printfln("Tesing: method=%s, nThread: %d, ElSize: %d", mth, nThread, elSize)
      out := BashEval("java Benchmark %s 1000 %d %d | grep Total", mth, nThread, elSize)
      out = TrimSpace(out[7:])
      Println(out)
      results[iMth][iThread][iElSize] = I(out)
    }
  }
}

Printfln("Method,Threads,ElSize,Ms")
for iElSize, elSize := range ElSizes {
  for iMth, mth := range Methods {
    for iThread, nThread := range Threads {
      Printfln("%v,%v,%v,%v", mth, nThread, elSize, results[iMth][iThread][iElSize])
    }
  }
}

