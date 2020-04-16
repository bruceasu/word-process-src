#! /usr/bin/env python
# -*- coding: utf-8 -*-
# join
# code word1
# code word2
# to code word1 word2
# 
try:
  import psyco

  psyco.full()
  print 'psyco activated.'
except:
  pass

import os
import sys
import optparse


def debug(*what):
  print >> sys.stderr, u'[DEBUG]: ', u' '.join(map(unicode, what))


usage = 'usage: %prog [options]'
parser = optparse.OptionParser(usage)
parser.add_option('-i', '--input', dest='input_file', help='specify input dict file.', metavar='FILE')
parser.add_option('-o', '--output', dest='output_file', help='specify output dict file, stdout if not set.')
parser.add_option('-e', '--encode', dest='file_encode', help='encode of dict file, UTF-8 if not set.')

options, args = parser.parse_args()

f = open(options.input_file, 'r')
f2 = sys.stdout
fenc = 'utf-8'
d = {}

if options.output_file:
  f2 = open(options.output_file, 'w')

if options.file_encode:
  fenc = options.file_encode

for line in f:
  x = line.strip().decode(fenc)

  if not x or x.startswith(u'#'):
    continue

  ls = x.split(None)
  if not ls[0] in d:
    d[ls[0]] = ls[1]
  else:
    d[ls[0]] = d[ls[0]] + u" " + ls[1]

for k, v in d.items():
  print >> f2, (k + u"\t" + v).encode(fenc)

f.close()
f2.close()
