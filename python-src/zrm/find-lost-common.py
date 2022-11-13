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

def find_lost_common(src_file, base_file, out_file):
    src = open(src_file)
    base = open(base_file)
    out = open(out_file, 'w')

    check = set()
    # make base dict
    for line in src:
        line = line.strip().decode('utf-8')
        check.add(line)    
    src.close()        

    for line in base:
        line = line.strip().decode('utf-8')
        kv = line.split()
        if not len(kv) == 2:
            continue
        if kv[0] in check:
            # found
            print >> out, line.encode("utf-8")
    base.close()
    out.close()
    
if __name__ == '__main__':
    encoding = 'utf-8'
    check = "data/check.txt"
    zrm = "data/zrm.dict.txt"
    out = "out/check-out.txt"
    find_lost_common(check, zrm, out)
    sort_file('out/check-out.txt') 
    # sort_file('out\\zrm.single.merged-full.txt')
    # sort_file('out\\zrm.single.merged-ci.txt')

    print 'All DONE!'
