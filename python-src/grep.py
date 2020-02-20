#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys

keyset = set()
encoding = 'utf-8'
common = '../data/8300.txt'
in_file = '../data/he_9000.txt'
(short_name, extension) = os.path.splitext(in_file)
out_file1 = '%s-in%s' % (short_name, extension)
out_file2 = '%s-not-in%s' % (short_name, extension)

print "Begin to load", common
ks = open(common, 'r')
for line in ks:
    line = line.strip().decode(encoding)
    keyset.add(line)
ks.close()

output_file_in = open(out_file1, "w")
output_file_not_in = open(out_file2, "w")
input_file = open(in_file, 'r')

print "Begin to process", in_file

count = 0
for line in input_file:
    line = line.strip().decode(encoding)
    if line == '' or line[0] == '#':
        continue
    kv = line.split()
    if kv[0] in keyset:
        print >> output_file_in, line.encode(encoding)
        count += 1
    else:
        print >> output_file_not_in, line.encode(encoding)
input_file.close()
output_file_in.close()
output_file_not_in.close()
print 'write to',  out_file1, count, 'lines.'

