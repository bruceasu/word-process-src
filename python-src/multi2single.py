#! /usr/bin/env python
# -*- coding: utf-8 -*-
# split
# code word1 word2
# to
# code word1
# code word2

try:
    import psyco

    psyco.full()
    print 'psyco activated.'
except Exception, e:
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

if options.output_file:
    f2 = open(options.output_file, 'w')

if options.file_encode:
    fenc = options.file_encode

for line in f:
    x = line.strip().decode(fenc)
    if not x or x.startswith(u'#'):
        continue

    length = len(x)
    ls = x.split(None)
    for y in range(1, len(ls)):
        print >> f2, (ls[0] + u"\t" + ls[y]).encode(fenc)

print "DONE!"
f.close()
f2.close()
