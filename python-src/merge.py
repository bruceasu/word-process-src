#!/usr/bin/env python
# -*coding: utf-8 -*-
import sys
import os


def merge(first, last, output=None, output_not_in_first=None, output_not_in_last=None, encoding='utf-8'):
    (short_name, extension) = os.path.splitext(first)
    if not output:
        output = '%s.merged%s' % (short_name, extension)
    if not output_not_in_first:
        output_not_in_first = '%s.output_not_in_first%s' % (short_name, extension)
    if not output_not_in_last:
        output_not_in_last = '%s.output_not_in_last%s' % (short_name, extension)

    f_first = open(first)
    f_last = open(last)
    f_out_main = open(output, 'w')
    f_out_not_in_first = open(output_not_in_first, 'w')
    f_out_not_in_last = open(output_not_in_last, 'w')

    print '%s: load %s ...' % (sys.argv[0], last)
    dict_map = {}
    for line in f_last:
        line = line.strip().decode(encoding)
        if line == '' or line[0] == '#':
            continue
        kv = line.split(None)
        dict_map[kv[0]] = kv[1]
        # dict_map[kv[0]] = ''.join(map(lambda x : x.replace('1', 'e')
        #                       .replace('2', 'i')
        #                       .replace('3', 'y')
        #                       .replace('4', 'r')
        #                       .replace('5', 'w')
        #                       .replace('6', 'u')
        #                       .replace('7', 't')
        #                       .replace('8', 'o'),
        #                       kv[1]))

    print '%s: merge %s an %s to %s' % (sys.argv[0], first, last, output)

    s = set()
    for line in f_first:
        line = line.strip().decode(encoding)
        # print line
        if line == '' or line[0] == '#':
            continue
        kv = line.split(None)
        s.add(kv[0])
        if kv[0] in dict_map:
            print >> f_out_main, (u"%s\t%s:%s" % (
                kv[0], kv[1], dict_map[kv[0]])).encode(encoding)
        else:
            print >> f_out_not_in_last, line.encode(encoding)

    print '%s: output %s.' % (sys.argv[0], output_not_in_last)

    f_last.seek(0)
    for line in f_last:
        line = line.strip().decode(encoding)
        if line == '':
            continue
        elif line[0] == '#':
            print >> f_out_not_in_first, line.encode(encoding)
            continue

        kv = line.split(None)
        if kv[0] not in s:
            print >> f_out_not_in_first, (u"%s\t:%s" % (kv[0], kv[1])).encode(
                encoding)
    print '%s: output %s.' % (sys.argv[0], output_not_in_first)
    f_last.close()
    f_first.close()
    f_out_main.close()
    f_out_not_in_first.close()
    f_out_not_in_last.close()


def main():
    py = '../data/abc-single.txt'
    # py = '../data/he_sp.sorted.txt'
    # py = u'../data/zrm.txt'
    # py = u'../data/sky.txt'
    # py = u'../data/wubi.2codes.txt'

    ext = '../data/bh-fuckup-last.txt'
    # ext = '../data/kuaishou.txt'
    # ext = u'../data/小雨点.txt'
    # ext = u'../data/he.txt'
    # ext = '../data/he.one.txt'
    output = r'../out/zyoy.single.merged.txt'
    output_not_in_py = r'../out/zyoy.single.merged.output_not_in_py.txt'
    output_not_in_ext = r'../out/zyoy.single.merged.output_not_in_ext.txt'
    encoding = 'utf-8'

    length = len(sys.argv)
    if length > 4:
        py = sys.argv[1]
        ext = sys.argv[2]
        output = sys.argv[3]
        encoding = sys.argv[4]
        (short_name, extension) = os.path.splitext(output)
        output_not_in_py = '%s.output_not_in_py%s' % (short_name, extension)
        output_not_in_ext = '%s.output_not_in_ext%s' % (short_name, extension)
    elif length > 3:
        py = sys.argv[1]
        ext = sys.argv[2]
        output = sys.argv[3]
        (short_name, extension) = os.path.splitext(output)
        output_not_in_py = '%s.output_not_in_py%s' % (short_name, extension)
        output_not_in_ext = '%s.output_not_in_ext%s' % (short_name, extension)
    elif length > 2:
        py = sys.argv[1]
        ext = sys.argv[2]
        (short_name, extension) = os.path.splitext(py)
        output = '%s.merged%s' % (short_name, extension)
        output_not_in_py = '%s.output_not_in_py%s' % (short_name, extension)
        output_not_in_ext = '%s.output_not_in_ext%s' % (short_name, extension)
    else:
        pass

    merge(py, ext, output, output_not_in_py, output_not_in_ext, encoding)
    print "%s: done!" % sys.argv[0]


if __name__ == '__main__':
    main()
