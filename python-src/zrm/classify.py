#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys


def sort_file(source_file, charset='utf-8'):
    #dir_name = os.path.dirname(source_file)
    #temp_file = os.path.join(dir_name, 'temp.txt')
    temp_file = 'temp.txt'
    output = os.popen('sort3.py %s %s %s' % (source_file, temp_file, charset))
    print output.read()
    import shutil
    shutil.move(temp_file, source_file)
    # open(source_file, "wb").write(open(temp_file, "rb").read())
    # os.remove(temp_file)

def classify():
    src = open("data/zrxm.txt")
    out1 = open("out/1.txt", "w")
    out2 = open("out/2.txt", "w")
    base = open("data/common-words.txt")
    check = set()
    # make base dict
    for line in base:
        line = line.strip().decode('utf-8')
        check.add(line)    
    base.close()        

    for line in src:
        line = line.strip().decode('utf-8')
        kv = line.split()
        if not len(kv) == 2:
            continue
        if kv[0] in check:
            # found
            print >> out1, line.encode("utf-8")
        else:
            print >> out2, line.encode("utf-8")
    src.close()
    out1.close()
    out2.close()
    
    out3 = open("out/3.txt", "w")
    base = open("data/zrxn-zi.txt")
    src = open("data/zrm.dict.s.txt")
     # make base dict
    for line in base:
        line = line.strip().decode('utf-8')
        check.add(line)    
    base.close()        

    for line in src:
        line = line.strip().decode('utf-8')
        kv = line.split()
        if not len(kv) == 2:
            continue
        if kv[0] in check:
            # found
            pass
        else:
            print >> out3, line.encode("utf-8")
    src.close()
    out3.close()
    
if __name__ == '__main__':
    classify()
    sort_file('out/1.txt') 
    sort_file('out/2.txt')
    sort_file('out/3.txt')

    print 'All DONE!'
