#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys
import merge as m
import make_short_for_zrm as ms


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
def merge(first, last_file='data/rain.txt', charset="utf-8"):
    print "last file: ", last_file
    merged_file = 'out/zrm.single.merged.txt'
    m.merge(first, last_file, merged_file, encoding=charset)
    return merged_file


def make_short(merged_file, charset='utf-8'):
    ms.make_short(merged_file, charset)


if __name__ == '__main__':
    encoding = 'utf-8'
    py = "data/zrm.txt"
    # merge
    ext = "data/rain.txt"
    merged = merge(py, ext, encoding)
    # make short
    make_short(merged, encoding)
    sort_file('out\\zrm.single.merged-out.txt')
    sort_file('out\\zrm.single.merged-full.txt')
    sort_file('out\\zrm.single.merged-ci.txt')

    print 'All DONE!'
