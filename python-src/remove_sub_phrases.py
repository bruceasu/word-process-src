#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
import os
from SimplePhrasesSplitter import SimplePhrasesSplitter

encoding = 'utf-8'
# infile = '../data/100000-phrases.txt'
infile = '../data/phrases.min.txt'
(short_name, extension) = os.path.splitext(infile)
output_file = open('%s-out%s' % (short_name, extension), "w")
output_file2 = open('%s-out2%s' % (short_name, extension), "w")
input_file = open(infile, 'r')

print "Begin to process", infile
count=0
remains = []
ok_phrases = []
ok_phrases_set = set()

count = 1
print 'add two words phrases...'
for line in input_file:
    line = line.strip().decode(encoding)
    if line == '' or line[0] == '#':
        continue
    length = len(line)
    if length == 2 and line not in ok_phrases_set:
        ok_phrases.append(line)
        ok_phrases_set.add(line)
        count += 1
    else:
        remains.append(line)
    if count % 10000 == 0:
        print 'processed two words: ', count
input_file.close()
print 'processed two words: ', count
# load simple split phrases

splitter = SimplePhrasesSplitter()
splitter.add_phrases_to_dict(ok_phrases_set)
print 'processing more than two words phrases...'
for line in remains:
    tokens = splitter.tokenize(line)
    length = len(line)
    if length == 3:
        if len(tokens) > 1:
            ok_phrases.append(line)
            ok_phrases_set.add(line)
            splitter.add_phrase_to_dict(line)
        else:
            print >>output_file2, line.encode('utf-8')
        # 大多是人名
        # print >>output_file2, line.encode('utf-8')
    elif length == 4:
        if len(tokens) == 4 or len(tokens) == 3:
            ok_phrases.append(line)
            ok_phrases_set.add(line)
            splitter.add_phrase_to_dict(line)
        else:
            print >>output_file2, line.encode('utf-8')
        pass
    elif length == 5:
        if len(tokens) == 5 or len(tokens) == 4:
            ok_phrases.append(line)
            ok_phrases_set.add(line)
            splitter.add_phrase_to_dict(line)
        else:
            print >>output_file2, line.encode('utf-8')
    else:
        if len(tokens) == length or len(tokens) == length -1 :
            ok_phrases.append(line)
            ok_phrases_set.add(line)
            splitter.add_phrase_to_dict(line)
        else:
           print >>output_file2, line.encode('utf-8')
    count += 1
    if count % 10000 == 0:
        print 'processed more than two words:', count

print 'processed', count
output_file2.close()
for line in ok_phrases:
    print >>output_file, line.encode('utf-8')
output_file.close()

print "DONE!"
