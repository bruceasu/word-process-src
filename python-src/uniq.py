#!/usr/bin/env python
# -*- codingL utf-8 -*-

__author__ = 'victor'


import sys


f1 = None
f2 = None


def usage():
    print '''usage:
    sys.argv[0] <in_file> <out_file> [encoding]
    in_file input file
    out_file output file
    encoding file encoding
    '''


def main():
    if len(sys.argv) < 3:
        usage()
        sys.exit(1)
    f1 = sys.argv[1]
    f2 = sys.argv[2]

    print 'processing', f1
    line_set = set()
    fin = open(f1)
    fout = open(f2, 'w')
    for line in fin:
        try:
            line = line
            if line not in line_set:
                line_set.add(line)
                fout.write(line)
        except:
            print line, 'cause error.'
    fin.close()
    fout.close()

    print 'DONE! Save file to ', f2

if __name__ == '__main__':
    main()

