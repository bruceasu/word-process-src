#!/usr/bin/env python
# -*- coding: utf-8 -*-
#  全拼转双拼(小鹤)

try:
    import psyco
    psyco.full()
    print 'psyco activated.'
except Exception, e:
    pass

import sys
import optparse

def debug(*what):
    print >> sys.stderr, u'[DEBUG]: ', u' '.join(map(unicode, what))

def make_table():
    s = {}
    w = {}
    m = {}
    s[u"q"]=u"q"
    s[u"w"]=u"w"
    s[u"r"]=u"r"
    s[u"t"]=u"t"
    s[u"y"]=u"y"
    s[u"sh"]=u"u"
    s[u"ch"]=u"i"
    s[u"p"]=u"p"
    s[u"s"]=u"s"
    s[u"d"]=u"d"
    s[u"f"]=u"f"
    s[u"g"]=u"g"
    s[u"h"]=u"h"
    s[u"j"]=u"j"
    s[u"k"]=u"k"
    s[u"l"]=u"l"
    s[u"z"]=u"z"
    s[u"x"]=u"x"
    s[u"c"]=u"c"
    s[u"zh"]=u"v"
    s[u"b"]=u"b"
    s[u"n"]=u"n"
    s[u"m"]=u"m"

    w[u"iu"]=u"q"
    w[u"ei"]=u"w"
    w[u"e"]=u"e"
    w[u"uan"]=u"r"
    w[u"van"]=u"r"
    w[u"ve"]=u"t"
    w[u"ue"]=u"t"
    w[u"üe"]=u"t"
    w[u"un"]=u"y"
    w[u"vn"]=u"y"
    w[u"u"]=u"u"
    w[u"i"]=u"i"
    w[u"o"]=u"o"
    w[u"uo"]=u"o"
    w[u"ie"]=u"p"

    w[u"a"]=u"a"
    w[u"ong"]=u"s"
    w[u"iong"]=u"s"
    w[u"ai"]=u"d"
    w[u"en"]=u"f"
    w[u"eng"]=u"g"
    w[u"ang"]=u"h"
    w[u"an"]=u"j"
    w[u"ing"]=u"k"
    w[u"uai"]=u"k"
    w[u"uang"]=u"l"
    w[u"iang"]=u"l"

    w[u"ou"]=u"z"
    w[u"ua"]=u"x"
    w[u"ia"]=u"x"
    w[u"ao"]=u"c"
    w[u"v"]=u"v"
    w[u"ui"]=u"v"
    w[u"in"]=u"b"
    w[u"iao"]=u"n"
    w[u"ian"]=u"m"

    for k1,v1 in s.items():
        for k2, v2 in w.items():
            m[k1 + k2] = v1 + v2
    m[u"ang"] = u"ag"
    m[u"eng"] = u"eg"
    m[u"a"] = u"aa"
    m[u"e"] = u"ee"
    m[u"o"] = u"oo"
    return m

def makeSP(fin, fout, fenc, with_tone=False):
    mapping = make_table()
    cnt = 0
    for line in f:
        #print line
        x = line.strip().decode(fenc)
        cnt += 1
        if not x or x.startswith(u'#'):
            continue
        data = x.split(None)
        if len(data[1]) == 1:
            print >> f2, (data[0] + u"\t" + data[1] + data[1] + u"\t" + data[2]).encode(fenc)
        elif len(data[1]) == 2:
            print >> f2,  (data[0] + u"\t" + data[1] + u"\t" + data[2]).encode(fenc)
        else:
            if mapping.has_key(data[1]):
                print >> f2, (data[0] + u"\t" + mapping[data[1]] + u"\t" + data[2]).encode(fenc)
            else:
                print "line", cnt, "key not in mapping", data[1]
usage = 'usage: %prog [options]'
parser = optparse.OptionParser(usage)
parser.add_option('-i', '--input', dest='input_file', help='specify full dict file.', metavar='FILE')
parser.add_option('-o', '--output', dest='output_file', help='specify output dict file, stdout if not set.')
parser.add_option('-e', '--encode', dest='file_encode', help='encode of dict file, UTF-8 if not set.')
parser.add_option('-t', '--with-tone', dest='with_tone', help='with tone.', action="store_true")

options, args = parser.parse_args()

f = open(options.input_file, 'r')
f2 = sys.stdout
fenc = 'utf-8'

if options.output_file:
    f2 = open(options.output_file, 'w')

if options.file_encode:
    fenc = options.file_encode

makeSP(f, f2, fenc)

f.close()
f2.close()

