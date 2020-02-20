#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys

keyset = set()
encoding = 'utf-8'
common = u'../../words-and-phrases-data/common-data/han-level-1.txt'
in_file = u'jjm/jjm.s.txt'
out_file = u'jjm/jjm-common.txt'
out_file2 = u'jjm/jjm-not-common.txt'

print "Begin to load", common
ks = open(common, 'r')
for line in ks:
    line = line.strip().decode(encoding)
    keyset.add(line)
ks.close()

output_file = open(out_file, "w")
output_file2 = open(out_file2, "w")
input_file = open(in_file, 'r')

print "Begin to process", in_file

count = 0
for line in input_file:
    line = line.strip().decode(encoding)
    if line == '' or line[0] == '#':
        continue
    kv = line.split(None)
    if kv[0] in keyset:
        print >> output_file, line.encode(encoding)
        count += 1
    else:
        print >> output_file2, line.encode(encoding)

input_file.close()
output_file.close()
output_file2.close()
print 'write to',  out_file, count, 'lines.'

