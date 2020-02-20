#!/usr/bin/env python
# -*- codingL utf-8 -*-

import sys
import os

__author__ = 'victor'

f1 = None
f2 = None
f3 = None


def usage():
    print '''usage:
    sys.argv[0] <in_file>
    '''


def main():
    if len(sys.argv) < 2:
        usage()
        sys.exit(1)
    f1 = sys.argv[1]
    (short_name, extension) = os.path.splitext(f1)
    f2 = '%s.single%s' % (short_name, extension)
    f3 = '%s.remain%s' % (short_name, extension)
    print 'processing', f1
    fin = open(f1)
    fout = open(f2, 'w')
    fout2 = open(f3, 'w')
    count = 0
    for line in fin:
        count += 1
        if count % 100 == 0:
            print 'processed %s' % count
        try:
            line = line.decode('utf-8').strip()
            if line == u'':
                continue
            kv = line.split(u'\t')
            if len(kv[0]) == 1:
                print >> fout, line.encode('utf-8')
            else:
                print >> fout2, line.encode('utf-8')
        except Exception, e:
            print line, 'cause error.'
    fin.close()
    fout.close()
    fout2.close()
    print 'processed %s' % count
    print 'DONE! Save file to %s and %s.' % (f2, f3)

if __name__ == '__main__':
    main()

