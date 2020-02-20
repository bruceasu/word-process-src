#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
import os

__author__ = 'victor'

def usage():
    print 'usage: sys.argv[0] in_file out_file'


def get_map_of_Jyutping():
    ''' 粤拼
    :return: {}
    '''
    f = {
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
        "h": "h",
        "ng": "~",
        "z": "z",
        "c": "c",
        "s": "s",
        "j": "j",
        "w": "w",
        "gw": "#",
        "kw": "$"
    }

    y = {
        "": "",
        "aa": "a",
        "a": "r",
        "e": "e",
        "yu": "v",
        "u": "u",
        "i": "i",
        "o": "o",
        "eo": "!",
        "oe": "@"
    }

    mei = {
        "": "",
        "p": "p",
        "t": "t",
        "k": "k",
        "m": "m",
        "n": "n",
        "ng": "~"
    }

    t = {
        "1": ",",
        "2": ".",
        "3": "/",
        "4": ",",
        "5": ".",
        "6": "/"
    }

    m = {}
    for k1, v1 in f.items():
        for k2, v2 in y.items():
            for k3, v3 in mei.items():
                for k4, v4 in t.items():
                    m["%s%s%s%s" % (k1, k2, k3, k4)] = "%s%s%s%s" % (
                        v1, v2, v3, v4)
    return m


def get_map_of_zyoy():
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
        "#": "x",  # gw
        "$": "q",   # kw
        "v": "v",   # zh
        "i": "i",   # ch
        "u": "u",   # sh
    }
    w = {
        "": "",
        "a": "a",  # aa
        "r": "r",  # a
        "e": "e",
        "v": "v",  # yu , 同u合并，kut,kvt, gun, gvn 会混，但方言中，两组音也是混的，关系不大。
        "u": "u",  # 长u，于短u是互补的。
        "i": "i",  # 长i，于短i是互补的。
        "o": "l",
        "!": "x",  # eo    eo, eu是互补的。
        "@": "x",  # oe/eu

        # "am": "q",
        # "rm": "r",
        # "im": "w",
        # "an": "g",
        # "rn": "f",
        # "un": "s",
        # "in": "d",
        # "on": "x",
        # "!n": "z",
        # "vn": "c",
        # "a~": "h",
        # "r~": "n",
        # "e~": "b",
        # "@~": "m",
        # "u~": "j",
        # "i~": "k",
        # "o~": "l"
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


def mapping(f1, f2, encoding='utf-8', m=None):
    input_file = open(f1)
    output_file = open(f2, 'w')
    if not m:
        # m = get_map_of_Jyutping()
        m = get_map_of_zyoy()

    count = 0
    for line in input_file:
        count += 1
        line = line.strip().decode(encoding)
        if line == '' or line[0] == '#':
            continue
        kv = line.split("\t")
        if len(kv) != 2:
            print line
            continue
        # if kv[1] in m:
        #     v = m[kv[1]]
        #     print >> output_file, (u"%s\t%s" % (kv[0], v)).encode(encoding)
        codes = kv[1].split()
        error = False
        new_codes = []
        for code in codes:
            if code in m:
                new_codes.append(m[code])
            else:
                error = True
                print '%s: no mapping!' %(codes,)
                break
        if error:
            print '%s: no mapping!' %count
        else:
            code_line = " ".join(new_codes)
            print >> output_file, (u"%s\t%s" % (kv[0], code_line)).encode(encoding)
        if count % 100 == 0:
            print '%s: processed %s words.' % (sys.argv[0], count)
    input_file.close()
    output_file.close()
    print '%s: processed %s words.' % (sys.argv[0], count)
    print '%s: save to %s' % (sys.argv[0], f2)


def main():
    f1 = '..\data\zyoy.single.txt'
    f2 = "..\data\zyoy.single.mapped.txt"
    encoding = 'utf-8'
    length = len(sys.argv)
    if length > 2:
        f1 = sys.argv[1]
        f2 = sys.argv[2]
    elif length > 1:
        f1 = sys.argv[1]
        (short_name, extension) = os.path.splitext(f1)
        f2 = '%s.mapped%s' % (short_name, extension)

    mapping(f1, f2, encoding)
    print '%s: DONE!' % sys.argv[0]


if __name__ == '__main__':
    main()
