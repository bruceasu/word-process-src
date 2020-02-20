#!/usr/bin/env python
# -*- coding: utf-8 -*-


import sys
import os

__author__ = 'victor'


def usage():
    print '''usage:
    sys.argv[0] <base_file> <check_file>
    the intersection of base_file and check_file will write to out1.txt
    the difference from check_file to base_file will write to out2.txt
    the difference from base_file to check_file will write to out3.txt
    the union of base_file and check_file will write to out4.txt
    '''


def load_file(file_name, encoding='utf-8'):
    """ load a file lines to set

    :param file_name: input file name
    :param encoding:  input file encoding
    :return: set()
    """
    fin = open(file_name)
    lines = set()
    for line in fin:
        line = line.strip().decode(encoding)
        lines.add(line)
    fin.close()
    return lines


def write_file(file_name, data_set, encoding='utf-8'):
    fout = open(file_name, 'w')
    for line in data_set:
        print >> fout, line.encode(encoding)
    fout.close()


def main():
    if len(sys.argv) != 3:
        usage()
        sys.exit(1)
    f1 = sys.argv[1]
    f2 = sys.argv[2]

    base_lines = load_file(f1, 'utf-8')
    check_lines = load_file(f2)

    d1 = check_lines.difference(base_lines)
    d2 = base_lines.difference(check_lines)
    s = base_lines.intersection(check_lines)
    a = base_lines.union(check_lines)

    (short_name, extension) = os.path.splitext(f2)
    f_out1 = '%s-difference-to-base%s' % (short_name, extension)
    f_out2 = '%s-difference-to-check%s' % (short_name, extension)
    f_out3 = '%s-intersection%s' % (short_name, extension)
    f_out4 = '%s-union%s' % (short_name, extension)
    write_file(f_out1, d1)
    write_file(f_out2, d2)
    write_file(f_out3, s)
    write_file(f_out4, a)

    print '''
    the intersection of base_file and check_file will write to %s
    the difference from check_file to base_file will write to %s
    the difference from base_file to check_file will write to %s
    the union of base_file and check_file will write to %s''' % (
    f_out1, f_out2, f_out3, f_out4)


if __name__ == '__main__':
    main()
