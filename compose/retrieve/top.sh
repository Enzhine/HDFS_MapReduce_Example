#!/usr/bin/env python3
import sys

store = []
file = sys.argv[1]
try:
  length = int(sys.argv[2])
except IndexError:
  length = 0

with open(file) as f:
  for line in f.readlines():
    k, v, *_ = line.split('\t')
    if len(k) >= length:
      store.append((k, int(v)))

store.sort(key = lambda x: x[1], reverse = True)
for sub in store[:10]:
  print(sub)