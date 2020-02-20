#! /usr/bin/env python
# -*- coding: utf-8 -*-
try:
    import psyco
    psyco.full()
    print 'psyco activated.'
except Exception, e:
    pass

import os
import sys
import optparse
import logging
logging.basicConfig(level=logging.DEBUG,
                format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s %(message)s',
                datefmt='%a, %d %b %Y %H:%M:%S')

def debug(*what):
    print >> sys.stderr, u'[DEBUG]: ', u' '.join(map(unicode, what))

usage = 'usage: %prog [options] prefix'
parser = optparse.OptionParser(usage)
parser.add_option(
    '-s', '--source', dest='source_file', default='phrases.txt',
    help='specify source dict file.', metavar='FILE')
parser.add_option(
    '-o', '--output', dest='output_file', default='${prefix}-phrases.txt',
    help='specify output file.', metavar='FILE')
parser.add_option(
    '-k', '--keyword-dir', dest='kw_dir', default='data',
    help='specify keyword set directory.', metavar='DIR')
parser.add_option(
    '-p', '--precise', action='store_true', dest='precise',
    default=False, help='generate comments on interpreted words only.')
parser.add_option(
    '-n', '--nofreq', action='store_true', dest='nofreq',
    default=False, help='pharses can without a frequency num.')
parser.add_option(
    '-v', '--verbose', action='store_true', dest='verbose',
    default=False, help='make lots of notice.')
options, args = parser.parse_args()

if len(args) < 1:
    parser.error('missing prefix')
prefix = args[0]

max_word_length = 0
word_map = dict()

keyword_file = open('%s/%s-keywords.txt' % (options.kw_dir, prefix))
for line in keyword_file:
    x = line.strip().decode('utf-8')
    if not x or x.startswith(u'#'):
        continue
    try:
        k, w = x.split(u'\t', 1)
    except Exception:
        print x
        continue
    
    if w in word_map:
        word_map[w].add(k)
    else:
        word_map[w] = set([k])
    if len(w) > max_word_length:
        max_word_length = len(w)
keyword_file.close()

if options.verbose:
    print '%s: max-word-length: %d' % (sys.argv[0], max_word_length)

source_file = open(options.source_file)
print '%s: create phrases from %s' % (sys.argv[0], options.source_file)

if (options.output_file == '${prefix}-phrases.txt'):
    options.output_file = '%s-phrases.txt' % prefix
phrase_file = open(options.output_file, 'w')
print '%s: write to %s with %s' % (sys.argv[0], options.output_file, prefix)
print >> phrase_file, '# %s phrase file generated by %s' % (prefix, sys.argv[0])

count = 0
phrase = u''
freq = 0


def output_phrase(keywords, words):
    global freq
    delimiter = u'' if all([len(w) == 1 for w in words]) else u' '
    k = u' '.join(keywords)
    p = delimiter.join(words)
    print >> phrase_file, (u'%s\t%d\t%s' % (p, freq, k)).encode('utf-8')


def g(keywords, words, start):
    global count, phrase
    if start == len(phrase):
        output_phrase(keywords, words)
        count += 1
        return
    for i in range(start, min(len(phrase), start + max_word_length)):
        w = phrase[start:i + 1]
        if w in word_map:
            for k in word_map[w]:
                g(keywords + [k], words + [w], i + 1)
counter = 0
for line in source_file:
    counter += 1
    try:
        x = line.strip().decode('utf-8')
        if not x or x.startswith(u'#'):
            continue
        phrase, freq_str = x.split(None, 1)
        freq = int(freq_str)
    except Exception:
        if options.nofreq:
            phrase = x
            freq = 0
        else:
            print >> sys.stderr, u'%s: error: invalid format (%s) %s' % (sys.argv[0], x, options.source_file)
            continue

    # exit()
    try:
        count = 0 
        g([], [], 0)
        if count == 0:
            if options.verbose:
                print (u'%s: phrase %s is not interpreted.' % (sys.argv[0], phrase)).encode('gb18030')
            if not options.precise:
                print >> phrase_file, (u'# no possible interpretations for %s' % phrase).encode('utf-8')
        elif count > 1:
            print >> phrase_file, (
                u'# %d possible interpretations for %s' % (count, phrase)
            ).encode('utf-8')
    except Exception, e:
        print e

    if counter % 1000 == 0:
        print "%s: process %d phrases." % (sys.argv[0], counter)

print "%s: process %d phrases." % (sys.argv[0], counter)

source_file.close()
phrase_file.close()
print '%s: %s written.' % (sys.argv[0], options.output_file)
