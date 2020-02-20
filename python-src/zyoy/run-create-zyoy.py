#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys
import mapping
import make_short_for_zyoy as ms
import phrases_tools as pt
import merge as m

def sort_file(source_file, charset='utf-8'):
    dir_name = os.path.dirname(source_file)
    temp_file = os.path.join(dir_name, 'temp.txt')
    output = os.popen('../sort3.py %s %s %s' % (source_file, temp_file, charset))
    print output.read()
    import shutil
    shutil.move(temp_file, source_file)
    # open(source_file, "wb").write(open(temp_file, "rb").read())
    # os.remove(temp_file)

# 键盘布局
# Ķ w e ă t y u i o p
# a s d f g h ñ k l Ģ
# z ö c ü b n m , . /

def get_mapping():
    s = {
        "": "",
        "b": "b",
        "p": "p",
        "m": "m",
        "f": "f",

        "d": "d",
        "t": "t",
        "n": "n",
        "l": "l",

        "g": "g",
        "k": "k",
        "~": "j",  # ng
        "h": "h",

        "z": "z",
        "c": "c",
        "s": "s",

        "j": "y",
        "w": "w",

        "#": ";",  # gw
        "$": "q"   # kw
    }
    w = {
        "": "",
        "a": "a",  # aa
        "r": "r",  # a
        "!": "x",  # eo    eo, eu是互补的。
        "@": "x",  # oe/eu

        "u": "u",  # 长u，于短u是互补的。
        "i": "i",  # 长i，于短i是互补的。
        "o": "o",
        "e": "e",

        "v": "v",  # yu

        # # 鼻音
        # "an": "f",
        # "un": "w",
        # "vn": "w",
        # "on": "t",
        # "!n": "y",
        # "rn": "j",
        # "in": "b",
        #
        # "o~": "q",
        # "@~": "p",
        # "u~": "s",
        # "a~": "g",
        # "r~": "h",
        # "i~": "k",
        # "e~": "k",
        #
        # "im": "m",
        # "am": "f",
        # "rm": "j",
        #
        # 双元音
        # "ai": "a",
        # "ri": "s",
        # "ei": "d",
        # "oi": "f",
        # "ui": "g",
        # "!i": "h",
        # "au": "w",
        # "ru": "e",
        # "iu": "r",
        # "ou": "t",

        #
        # # 入音
        # "ak" : "a",
        # "rk": "r",
        # "ok": "o",
        # "ek": "e",
        # "ik": "i",
        # "uk" : "u",
        # "@k": "q",
        #
        # "at" : "5",
        # "rt" : "4",
        # "ut" : "7",
        # "it" : "8",
        # "vt" : "6",
        # "!t" : "0",
        # "ot" : "9",
        #
        # "ap": ",",
        # "rp": ".",
        # "ip": "/"
    }

    m = {
        "": "",
        "u": "u",
        "i": "i",
        "p": "p",
        "t": "t",
        "k": "k",
        "m": "m",
        "n": "n",
        "~": "j"  # ng
    }

    zyoy_mapping = {}
    for k1, v1 in s.items():
        for k2, v2 in w.items():
            for k3, v3 in m.items():
                zyoy_mapping["%s%s%s" % (k1, k2, k3)] = "%s%s%s" % (v1, v2, v3)
    return zyoy_mapping


def make_mapping():
    single = '..\..\..\words-and-phrases-data\cantonese-data\cantonese-common.single.sort.txt'
    py = "..\..\out\zyoy.single.mapped.txt"
    mapping.mapping(single, py, encoding, get_mapping())
    return py


def make_mapping_phrases():
    single = '..\..\..\words-and-phrases-data\cantonese-data\cantonese-ci.txt'
    py = "..\..\out\zyoy.prhases.mapped.txt"
    mapping.mapping(single, py, encoding, get_mapping())
    return py

def merge(first, last_file='..\..\..\words-and-phrases-data\bear-data\bear-s.txt', charset="utf-8"):
    last = last_file
    merged_file = r'../out/merged.txt'
    m.merge(first, last, merged_file, encoding=charset)
    return merged_file


def make_short(merged_file, charset='utf-8'):
    ms.make_short(merged_file, charset)


def make_short_phrases():
    output = os.popen('..\\make-phrases-short.py')
    print output.read()


def create_zyoy_keywords(in_file, out_file, charset='utf-8'):
    print "%s: create zyoy-keywords: %s from %s." % (sys.argv[0], out_file, in_file)
    ifile = open(in_file)
    ofile = open(out_file, 'w')
    count = 0
    for line in ifile:
        count += 1
        line = line.decode(charset).strip()
        if line == '' or line[0] == '#':
            continue
        kv = line.split()
        print >> ofile, ("%s\t%s" % (kv[1], kv[0])).encode(charset)
        if count % 1000 == 0:
            print '%s: processed %d words' %(sys.argv[0], count)
    print '%s: processed %d words' % (sys.argv[0], count)
    print "%s: %s written!" % (sys.argv[0], out_file)


def make_phrases(charset='utf-8'):
    # create zyoy-keywords.txt
    ci_file = r"../../out/merged-ci.txt"
    kw_file = r"../../zyoy-keywords.txt"
    create_zyoy_keywords(ci_file, kw_file, charset)
    phrase_file = r"../../../words-and-phrases-data/phrase-data/phrases.min.txt"
    output_file = '../../out/phrases.txt'
    output = os.popen('..\\make-phrases-zime.py -v -k %s -p -n -s %s -o %s zyoy' % ('..', phrase_file, output_file))
    print output.read()
    print 'make phrases for full symbols done!'
    return output_file


def load_single_word_code():
    in_file = open('../../out/zyoy.single.merged-out.txt')
    code_set = set()
    for line in in_file:
        line = line.strip().decode('utf-8')
        if line == '' or line[0] == '#':
            continue
        kv = line.split()
        code_set.add(kv[1])
    in_file.close()
    return code_set


def accept(phrase, code, order, d_count):
    global code_set
    if code not in code_set:
        return True


if __name__ == '__main__':
    encoding = 'utf-8'

    # mapping
    py = make_mapping()
    # make_mapping_phrases()
    # merge
    merged = merge(py, last_file="../../../words-and-phrases-data/wubi-data/wubi.2codes.txt", charset=encoding)
    # # make short
    make_short(merged, encoding)
    sort_file('../../out/merged-out.txt')
    sort_file('../../out/merged-full.txt')
    sort_file('../../out/merged-ci.txt')
    #
    # # make short phrases
    # make_short_phrases()
    #
    # # make phrases
    # phrases_file = make_phrases(encoding)
    #
    # # cleanup make phrases
    # out_file = pt.remove_tab(phrases_file, charset=encoding)
    # code_set = load_single_word_code()
    # pt.clean_dup_phrases(out_file, 10000, 1, 30000, 1, 1, filterFn=accept)
    print 'clean phrases done!'
