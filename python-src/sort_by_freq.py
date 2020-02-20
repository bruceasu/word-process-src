#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys


def usage():
    print '''usage:
    sys.argv[0] <in_file> <out_file> [encoding]
    '''
argc = len(sys.argv)
if argc < 3:
    usage()
    sys.exit(1)
f1 = sys.argv[1]
f2 = sys.argv[2]
if argc > 3:
    encoding = sys.argv[3]
else:
    encoding = 'utf-8'
fin = open('../data/sort-order.txt')
lines = {}
count = 1
for line in fin:
    line = line.strip().decode(encoding)
    if line not in lines:
        lines[line] = count
        count += 1

fin.close()


def load_first_3000_words():
    global line
    first3000_file = open('../data/gb2312-1.txt')
    print ("%s: Load first 3000 words." % sys.argv[0])
    first_3000_set = set()
    for line in first3000_file:
        line = line.strip().decode('utf-8')
        if line == "" or line[0] == '#':
            continue
        first_3000_set.add(line)
    first3000_file.close()
    return first_3000_set


first_3000_set = load_first_3000_words()

fin = open(f1)
data = list()
for line in fin:
    line = line.strip().decode(encoding)

    if line == '' or line[0] == '#':
        continue
    kv = line.split(None)
    if kv[0] in lines:
        score = lines[kv[0]]
        m = {}
        m['k'] = kv[0]
        m['line'] = line
        if kv[0] in first_3000_set:
            m['score'] = score
            m['line'] = line + u'#序' + str(score + 500000)
        else:
            m['score'] = score + 200000
            m['line'] = line + u'#序' + str(score * 10)
        data.append(m)
    else:
        score = 300000
        m = {}
        m['k'] = kv[0]
        m['line'] = line + u'#序' + str(score)
        m['score'] = score
        data.append(m)
fin.close()

data.sort(key=lambda x: x['score'])

fout = open(f2, 'w')
for e in data:
    print >> fout, e['line'].encode(encoding)
fout.close()
print 'done!'

