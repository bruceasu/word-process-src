#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import os.path
import make_phrases_for_table as mp
import make_short_for_table as ms
import phrases_tools as pt
import merge as m


def sort_file(source_file, charset='utf-8'):
    dir_name = os.path.dirname(source_file)
    temp_file = os.path.join(dir_name, 'temp.txt')
    output = os.popen('..\\sort3.py %s %s %s' % (source_file, temp_file, charset))
    print output.read()
    import shutil
    shutil.move(temp_file, source_file)
    # open(source_file, "wb").write(open(temp_file, "rb").read())
    # os.remove(temp_file)

def sort_word_file(source_file, charset='utf-8'):
    dir_name = os.path.dirname(source_file)
    temp_file = os.path.join(dir_name, 'temp.txt')
    output = os.popen('../sort_by_freq.py %s %s %s' % (source_file, temp_file, charset))
    print output.read()
    import shutil
    shutil.move(temp_file, source_file)
    # open(source_file, "wb").write(open(temp_file, "rb").read())
    # os.remove(temp_file)

def accept(phrase, code, order, d_count):
    global code_set
    if code not in code_set:
        return True


def make_short_phrases():
    output = os.popen('make-phrases-short.py')
    print output.read()


def load_single_word_code():
    in_file = open('../out/merged-out.txt')
    code_set = set()
    for line in in_file:
        line = line.strip().decode('utf-8')
        if line == '' or line[0] == '#':
            continue
        kv = line.split()
        code_set.add(kv[1])
    in_file.close()
    return code_set

if __name__ == '__main__':
    encoding = 'utf-8'

    # mapping
    last = u'../../../words-and-phrases-data/phrase-data/小雨点2.txt'
    # merge
    first = r"../../../words-and-phrases-data/phrase-data/he_sp.sorted.txt"
    merged = r'../../out/merged.txt'
    m.merge(first, last, merged, encoding=encoding)

    # make short
    ms.make_short(merged, encoding)

    sort_file('../../out/merged-out.txt')
    sort_file('../../out/merged-full.txt')
    sort_file('../../out/merged-ci.txt')

    # make_short_phrases()
    #
    # # make phrases
    # ci_file = r"../out/merged-ci.txt"
    # phrase_file = r"../../words-and-phrases-data/phrase-data/phrases.min.txt"
    # output_file = r'../out/phrases.txt'
    # err_file = r'../out/phrases-err.txt'
    #
    # mp.make_phrase(ci_file, phrase_file, output_file, err_file, encoding)
    # print 'make phrases done!'
    #
    # code_set = load_single_word_code()
    #
    # pt.clean_dup_phrases(output_file, 15000, 1, 30000, 1, 1, filterFn=accept)
    # print 'clean phrases done!'
