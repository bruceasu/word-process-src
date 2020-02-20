#!/usr/bin/env python2
# -*- coding:utf-8 -*-

import os
import sys
import logging
import optparse

try:
    import psyco

    psyco.full()
    print 'psyco activated.'
except Exception:
    pass

logging.basicConfig(level=logging.DEBUG,
                format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s %(message)s',
                datefmt='%a, %d %b %Y %H:%M:%S')


def debug(*what):
    print >> sys.stderr, u'[DEBUG]: ', u' '.join(map(unicode, what))


# if len(sys.argv) < 2:
#     logging.error('missing source file')
#     sys.exit(1)
# sourcefile = sys.argv[1]
in_file = r"..\..\words-and-phrases-data\phrase-data\100000-phrases.txt"
out_file = r'..\out\100000-phrases.out.txt'
source_file = open(in_file)
phrase_file = open(out_file, 'w')

phrase = u''
syllable_list = u''

# load keys
# keys_file = open('..\\out\\zyoy3.txt')
keys_file = open(r'..\out\merged-out.txt')
keys = {}
codes = set()
for line in keys_file:
    line = line.strip().decode('utf-8')
    if line == "" or line[0] == '#':
        continue
    # print line
    kv = line.split('\t')
    if len(kv) == 3:
        word, syllable_list, freq = kv
    elif len(kv) == 2:
        word, syllable_list = kv
    keys[word] = syllable_list[0]
    codes.add(syllable_list)
keys_file.close()
counter = 0
for line in source_file:
    counter += 1
    try:
        phrase = line.strip().decode('utf-8')
        if not phrase or phrase.startswith(u'#'):
            continue
    except Exception, e:
        try:
            logging.error(u'error: invalid format (%s) %s' % (phrase, in_file))
        except Exception, e:
            pass
        continue
    length = len(phrase)
    if counter % 1000 == 0:
        logging.info("%s: process %d phrases." % (sys.argv[0], counter))
    # if length == 2:
    #     if phrase[0] in keys and phrase[1] in keys:
    #         sl = keys[phrase[0]][0] + keys[phrase[1]][0]
    #         if sl not in codes:
    #             codes.add(sl)
    #             print >> phrase_file,\
    #                 (u'%s\t%s' % (phrase, sl)).encode('utf-8')
    #         else:
    #             pass
    #     else:
    #         pass
    # else:
    #     pass
    flag = True
    for zi in phrase:
        if zi not in keys:
            flag = False
            break
    if flag:
        c = ''
        for zi in phrase:
            c += keys[zi][0]
        # c += "'"
        if len(phrase) == 2:
            if c not in codes:
                codes.add(c)
                print >> phrase_file, (u'%s\t%s' % (phrase, c)).encode('utf-8')
            else:
                pass
        else:
            codes.add(c)
            print >> phrase_file, (u'%s\t%s' % (phrase, c)).encode('utf-8')



logging.info("%s: process %d phrases." % (sys.argv[0], counter))

source_file.close()
phrase_file.close()
print '%s: %s written.' % (sys.argv[0], out_file)
