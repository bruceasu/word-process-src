#!/usr/bin/env python
# -*- codingL utf-8 -*-

import sys
import os


def usage():
    print """usage:
    sys.argv[0] <in_file> <out_file> [encoding]
    """


def load_dic():
    """ read the frequency file.
    
    """
    f = open('../data/phrases-freq.txt')
    d = {}
    for line in f:
        line = line.strip().decode('utf-8')
        if line == '' or line[0] == '#':
            continue
        else:
            k, v = line.split()
            d[k] = int(v)
    return d


def load_phrases_code_file(data_file, charset='utf-8'):
    """ load the phrases file
    
    :param data_file: 
    :param charset: 
    :return: 
    """
    data = []
    dic = load_dic()
    fin = open(data_file)
    for line in fin:
        line = line.strip().decode(charset)
        if line == '' or line[0] == '#':
            continue
        kv = line.split(None)
        if kv[0] in dic:
            score = dic[kv[0]]
        else:
            score = 0
        m = {'k': kv[0], 'line': line, 'score': score}
        data.append(m)
    fin.close()
    return data


def save_result(o_file, charset='utf-8'):
    out_file = open(o_file, 'w')
    for e in data:
        print >> out_file, e['line'].encode(charset)
    out_file.close()


if __name__ == '__main__':
    encoding = 'utf-8'
    argc = len(sys.argv)
    if argc == 1:
        usage()
        sys.exit(1)
    elif argc == 2:
        f1 = sys.argv[1]
        (short_name, extension) = os.path.splitext(f1)
        f2 = '%s.sorted%s' % (short_name, extension)
    elif argc == 3:
        f1 = sys.argv[1]
        f2 = sys.argv[2]
    else:
        f1 = sys.argv[1]
        f2 = sys.argv[2]
        encoding = sys.argv[3]

    data = load_phrases_code_file(f1, encoding)
    data.sort(key=lambda x: x['score'], reverse=True)
    save_result(f2, encoding)
    print 'done!'

