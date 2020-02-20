#!/usr/bin/env python
# -*- coding: utf-8 -*-


import sys
import os

__author__ = 'victor'


def usage():
    print '''usage:
    sys.argv[0] <first_file> <second_file>
    '''


def load_file(file_name, encoding='utf-8'):
    """ load a file lines to dict

    :param file_name: input file name
    :param encoding:  input file encoding
    :return: set()
    """
    fin = open(file_name)
    lines = {}
    for line in fin:
        line = line.strip().decode(encoding)
        if line == '' or line[0] == '#':
            continue
        kv = line.split()
        lines[kv[0]] = kv[1]
    fin.close()
    return lines


def main():
    if len(sys.argv) != 4:
        usage()
        sys.exit(1)
    f1 = sys.argv[1]
    f2 = sys.argv[2]
    f3 = sys.argv[3]

    first_file_lines = load_file(f1, 'utf-8')
    second_file_lines = load_file(f2)
    out_file = open(f3, 'w')
    for k,v in first_file_lines.iteritems():
        if k in second_file_lines:
            v2 = second_file_lines[k]
        else:
            v2 = ''
        print >> out_file, ("%s\t%s%s" % (k, v, v2)).encode("utf-8")

    print ' join %s and %s to %s' % (f1, f2, f3)


if __name__ == '__main__':
    main()
