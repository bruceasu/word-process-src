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

def find_lost_full(src_file, base_file, out_file):
    src = open(src_file)
    base = open(base_file)
    out = open(out_file, 'w')

    baseMap = {}
    # make base dict
    for line in base:
        line = line.strip().decode('utf-8')
        kv = line.split()
        if not len(kv) == 2:
            continue
        if kv[0] in baseMap:
            baseMap[kv[0]].append(kv[1])
        else:
            baseMap[kv[0]] = [kv[1]]
    base.close()        

    # check src
    for line in src:
        line = line.strip().decode('utf-8')
        kv = line.split()
        if not len(kv) == 2:
            continue
        if len(kv[1]) == 4:
            # is full
            continue
        if kv[0] in baseMap:
            # found full
            vs = baseMap[kv[0]]
            for v in vs:
                if v.startswith(kv[1]):
                    print >> out, ("%s\t%s" % (kv[0], v)).encode("utf-8")
    src.close()
    out.close()
    
if __name__ == '__main__':
    encoding = 'utf-8'
    zrxm = "data/zrxm.txt"
    zrm = "data/zrm.dict.txt"
    out = "out/zrxm-full.txt"
    find_lost_full(zrxm, zrm, out)
    sort_file('out/zrxm-full.txt') 
    # sort_file('out\\zrm.single.merged-full.txt')
    # sort_file('out\\zrm.single.merged-ci.txt')

    print 'All DONE!'
