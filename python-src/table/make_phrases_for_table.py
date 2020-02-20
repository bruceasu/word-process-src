#!/usr/bin/env python
# -*- coding: utf-8 -*-
import sys
import os

__author__ = 'Bruce Suk'


def make_phrase(dict_file, phrase_file, out_file,  err_file, encoding='utf8'):
    print 'in file: %s\nphrases file: %s\nout file: %s\nerror file: %s\n' % (
        dict_file, phrase_file, out_file, err_file)
    # open files

    p_file = open(phrase_file)
    o_file = open(out_file, 'w')
    e_file = open(err_file, 'w')

    dict_map = load_dict_map(dict_file, encoding)

    count = 0
    for line in p_file:
        try:
            line = line.strip().decode(encoding)
            if line == '' or line[0] == '#':
                continue
            count += 1
            length = len(line)
            # print (line, length)
            codes = load(line, dict_map)
            if length == 1:
                pass
            elif length == 2:
                for c1 in codes[0]:
                    for c2 in codes[1]:
                        print >> o_file, (u"%s\t%s%s" % (
                            line, c1[0:2], c2[0:2])).encode(encoding)
            elif length == 3:
                for c1 in codes[0]:
                    for c2 in codes[1]:
                        for c3 in codes[2]:
                            print >> o_file, (u"%s\t%s%s%s" % (
                                line, c1[0], c2[0], c3[0:2])).encode(encoding)
            else:
                for c1 in codes[0]:
                    for c2 in codes[1]:
                        for c3 in codes[2]:
                            for c4 in codes[-1]:
                                print >> o_file, (u"%s\t%s%s%s%s" % (
                                    line, c1[0], c2[0], c3[0], c4[0])).encode(encoding)
            if count % 1000 == 0:
                print 'processed phrases:', count
        except Exception, err:
            print >> e_file, line.encode(encoding)

    p_file.close()
    o_file.close()
    e_file.close()


def load_dict_map(dict_file, encoding='utf8'):
    print'load ', dict_file, ' ...'
    f = open(dict_file)
    dict_map = {}
    for line in f:
        line = line.strip().decode(encoding)
        if line == '' or line[0] == '#':
            continue

        kv = line.split(None)
        # print kv
        if kv[0] in dict_map:
            if len(kv[1]) > len(dict_map[kv[0]]):
                dict_map[kv[0]] = kv[1]
        else:
            dict_map[kv[0]] = kv[1]
    f.close()
    return dict_map


def load(str, dict_map):
    codes = []
    for x in str:
        if x not in dict_map:
            raise Exception('no this word. %s in %s' % (x, str))
    for x in str:
        codes.append([dict_map[x]])

    return codes


def main():
    dict_file = r'..\..\out\merged-ci.txt'
    phrase_file = r'..\..\..\words-and-phrases-data\phrase-data\phrases.min.txt'
    encoding = 'utf-8'
    length = len(sys.argv)
    if length > 3:
        dict_file = sys.argv[1]
        phrase_file = sys.argv[2]
        encoding = sys.argv[3]
    if length > 2:
        dict_file = sys.argv[1]
        encoding = sys.argv[2]
    elif length > 1:
        dict_file = sys.argv[1]
    (shotname, extension) = os.path.splitext(dict_file)
    out_file = '%s-out%s' % (shotname, extension)
    err_file = '%s-out2%s' % (shotname, extension)
    make_phrase(dict_file, phrase_file, out_file, err_file, encoding)
    print('%s: DONE!' % sys.argv[0])


if __name__ == '__main__':
    main()
