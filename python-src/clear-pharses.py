#!/usr/bin/env python
# -*- coding: utf-8 -*-

# input_file = open("t.txt", encoding='utf_8_sig')
import sys
import os

encoding = 'utf-8'
two_words = {}
two_words_path = u"../src/main/resources/core.txt";
two_words_file = open(two_words_path)
print "Begin to load",  two_words_path
for w in two_words_file:
    w = w.strip().decode(encoding)
    two_words[w] = 1
two_words_file.close()

m_words_path =  u"../src/main/resources/p.txt";
input_file = open(m_words_path)
m_words_file = open(m_words_path + ".not-in", "w")
counter = 0
print "Begin to load", m_words_path
m = []
for line in input_file:
    counter += 1
    line = line.strip().decode(encoding)
    length = len(line)
    if counter % 10000 == 0:
        print "processed", counter, "lines."
    if length == 2:
        if line not in two_words:
            two_words[line] = 1
        print >> m_words_file, line.encode("utf-8")
    else:
        m.append(line)
input_file.close()
print ">>>>>>>>>>>>>>>>>>"
found = False
#for line in m:
#    for j in xrange(0, len(line)-1):
#        x = line[j:j+2]
#        if x in two_words:
#            found = True
#            break
#    if not found:
#        print >> m_words_file, line.encode("utf-8")
for line in m:
    found = line[0:2] in two_words or line[-2:] in two_words
    if not found:
        print >> m_words_file, line.encode("utf-8")
m_words_file.close()
print "DONE!"
