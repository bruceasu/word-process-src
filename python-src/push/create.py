#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys
import make_short as ms
import merge as m

encoding = 'utf-8'

def sort_file(source_file, charset='utf-8'):
    dir_name = os.path.dirname(source_file)
    temp_file = os.path.join(dir_name, 'temp.txt')
    sort_program = 'sort3.py'
    output = os.popen('%s %s %s %s' % (sort_program, source_file, temp_file, charset))
    print "sort %s with %s " % (source_file, temp_file)
    print output.read()
    import shutil
    shutil.move(temp_file, source_file)

    # open(source_file, "wb").write(open(temp_file, "rb").read())
    # os.remove(temp_file)


def merge(first, last):
    merged_file = 'out/merged.txt'
    m.merge(first, last, merged_file, encoding=encoding)
    return merged_file


# mapping
first = u'data\\single.txt'
last = u'data\\kk.txt'
# merge
merged_file = merge(first, last)

# make short
ms.make_short(merged_file, encoding)


sort_file('out/merged-out.txt')
sort_file('out/merged-full.txt')
sort_file('out/merged-ci.txt')
