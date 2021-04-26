#!/usr/bin/env python
# -*- coding: utf-8 -*-

# input_file = open("t.txt", encoding='utf_8_sig')
import sys
import os

encoding = 'utf-8'
gb2312 = {}
gb2312_path = u"rain-gb2312.txt";
gb2312_file = open(gb2312_path)
print "Begin to load",  gb2312_path
for w in gb2312_file:
    w = w.strip().decode(encoding)
    kv = w.split()
    gb2312[kv[0]] = kv[1]
gb2312_file.close()

in_path =  u"rain.txt";
input_file = open(in_path)
m_words_file = open("a.txt", "w")
counter = 0
print "Begin to load", in_path
m = []
for line in input_file:
    counter += 1
    line = line.strip().decode(encoding)
    kv = line.split()
    length = len(kv)
    if counter % 10000 == 0:
        print "processed", counter, "lines."
    if length == 2:
        if kv[0] not in gb2312:
            print >> m_words_file, line.encode("utf-8")
    else:
        pass
input_file.close()
m_words_file.close()
print "DONE!"
