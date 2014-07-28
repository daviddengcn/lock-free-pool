#!/bin/gosl

Methods := [...]string {
  "nopool", "sync", "lockfree", "fast",
}

Threads := [...]int {
  1, 2, 4, 8, 16, 32,
}

ElSizes := [...]int {
  1, 1000, 5000, 10000,
}

var results[len(Methods)][len(Threads)][len(ElSizes)]int

for iElSize, elSize := range ElSizes {
  for iMth, mth := range Methods {
    for iThread, nThread := range Threads {
      Printfln("Tesing: method=%s, nThread: %d, ElSize: %d", mth, nThread, elSize)
      var cmd string
      if mth == "fast" {
        cmd = S("java FastObjectPool 1000 %d %d | grep Total", nThread, elSize)
      } else {
        cmd = S("java Benchmark %s 1000 %d %d | grep Total", mth, nThread, elSize)
      }

      out := BashEval(cmd)
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

