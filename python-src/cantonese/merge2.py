#!/usr/bin/env python
# -*coding: utf-8 -*-
import sys
import os

def load_first_dict(dictFile, encoding='utf-8'):
    """ load the file as a dict.
    """
    f_dict = open(dictFile)
    s = {}
    for line in f_dict:
        line = line.strip().decode(encoding)
        # print line
        if line == '' or line[0] == '#':
            continue
        kv = line.split(None, 2)
        if len(kv) == 2:
            w = kv[0]
            v = kv[1]
            if w in s:
                s[w].append(v)
            else:
                s[w] = [v]
    f_dict.close()
    return s;

def load_last_dict(dictFile, encoding='utf-8'):
    """ load the file as a dict.
    """
    f_dict = open(dictFile)
    s = {}
    for line in f_dict:
        line = line.strip().decode(encoding)
        # print line
        if line == '' or line[0] == '#':
            continue
        kv = line.split("\t")
        if len(kv) == 2:
            w = kv[0]
            v = kv[1].split(None)
            for i in xrange(0, len(v)):
                if v[i] in s:
                    s[v[i]].append(w)
                else:
                    s[v[i]] = [w]
    f_dict.close()
    return s;

def open_output_files(output=None, output_not_in_first=None, output_not_in_last=None, short_name="out", extension = ".txt", encoding='utf-8'):
    if not output:
        output = '%s.merged%s' % (short_name, extension)
    if not output_not_in_first:
        output_not_in_first = '%s.output_not_in_first%s' % (short_name, extension)
    if not output_not_in_last:
        output_not_in_last = '%s.output_not_in_last%s' % (short_name, extension)
    # open output files.
    f_out_main = open(output, 'w')
    f_out_not_in_first = open(output_not_in_first, 'w')
    f_out_not_in_last = open(output_not_in_last, 'w')
    return f_out_main, f_out_not_in_first, f_out_not_in_last

def merge(firstDict, lastDict, output=None, output_not_in_first=None, output_not_in_last=None, short_name="out", extension = ".txt", encoding='utf-8'):
    """ merge the first and the last 
    """

    f_out_main, f_out_not_in_first, f_out_not_in_last = open_output_files(output, output_not_in_first, output_not_in_last, short_name, extension, encoding)
    print "firstDict keys: ", len(firstDict)
    print "lastDict keys: ", len(lastDict)
    print 'merging to ...', output
    for (k, v) in firstDict.iteritems():
        #print k, v, lastDict[k];
        try:
            if k in lastDict:
                lv = lastDict[k]
                for x1 in v:
                    for x2 in lv:
                        print >> f_out_main, (u"%s\t%s:%s" % (k, x1, x2)).encode(encoding)
            else:
                print >> f_out_not_in_last, line.encode(encoding)
        except:
            pass
    for k in lastDict.iterkeys():
        try:
            if k not in firstDict:
                for x in xrange(0, len(lastDict[k])):
                    print >> f_out_not_in_first, (u"%s\t:%s" % (k, lastDict[k][x])).encode(encoding)
        except:
            pass
    f_out_main.close()
    f_out_not_in_first.close()
    f_out_not_in_last.close()


def main():
    first = 'data\\ft2.txt'
    last = 'data\\bouseu.txt'
    output = r'out/test.merged.txt'
    output_not_in_first = 'out/test.merged.output_not_in_first.txt'
    output_not_in_last = 'out/test.merged.output_not_in_last.txt'
    encoding = 'utf-8'

    length = len(sys.argv)
    if length > 4:
        first = sys.argv[1]
        last = sys.argv[2]
        output = sys.argv[3]
        encoding = sys.argv[4]
        (short_name, extension) = os.path.splitext(output)
        output_not_in_first = '%s.output_not_in_first%s' % (short_name, extension)
        output_not_in_last = '%s.output_not_in_last%s' % (short_name, extension)
    elif length > 3:
        first = sys.argv[1]
        last = sys.argv[2]
        output = sys.argv[3]
        (short_name, extension) = os.path.splitext(output)
        output_not_in_first = '%s.output_not_in_first%s' % (short_name, extension)
        output_not_in_last = '%s.output_not_in_last%s' % (short_name, extension)
    elif length > 2:
        first = sys.argv[1]
        last = sys.argv[2]
        (short_name, extension) = os.path.splitext(py)
        output = '%s.merged%s' % (short_name, extension)
        output_not_in_first = '%s.output_not_in_first%s' % (short_name, extension)
        output_not_in_last = '%s.output_not_in_last%s' % (short_name, extension)
    else:
        pass
    (short_name, extension) = os.path.splitext(first)
    merge(load_first_dict(first, encoding), load_last_dict(last, encoding), output, output_not_in_first, output_not_in_last, short_name, extension, encoding)
    print "%s: done!" % sys.argv[0]


if __name__ == '__main__':
    main()
