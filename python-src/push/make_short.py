#!/usr/bin/env python
# -*- coding: utf-8 -*-

import logging
import sys
import os

def make_short(infile, coding='utf-8'):
    (short_name, extension) = os.path.splitext(infile)
    out_file = '%s-out%s' % (short_name, extension)
    out_full_file = '%s-full%s' % (short_name, extension)
    out_ci_file = '%s-ci%s' % (short_name, extension)

    input_file = open(infile)
    output_file = open(out_file, "w")
    output_full_file = open(out_full_file, "w")
    output_ci_file = open(out_ci_file, "w")

    print "Begin to process ", infile

    code_set = {}
    result = []
    full = []
    code_len_count = []
    for i in range(0, 30):
        code_len_count.append(0)

    remain = {}
    ci = set()
    count = 0
    padding = ['', ';', '/', ',', '.', '\'']

    linecount = 0
    for line in input_file:
        linecount += 1;
        count += 1
        line = line.strip().decode(coding)
        if line == "" or line[0] == '#':
            continue
        kv = line.split(None)
        w = kv[0]
        c = kv[1]
        cc = c.split(':')
        key_single = cc[0][0]
        key_first_full = cc[0]
        key_full = cc[0] + cc[1]
        length = len(cc[0])
        ci.add(w + '\t' + key_first_full)
        if key_single not in code_set:
            code_set[key_single] = 1
            code_len = len(key_single)
            code_len_count[code_len] += 1
            result.append(w + "\t" + key_single + "\t1")
            full.append("$ddcmd(" + w + "," + w + "[" + key_single + "])\t" + key_full)
        elif key_single in code_set and code_set[key_single] < 2:
            new_code = key_single + padding[code_set[key_single]]
            code_set[key_single] += 1
            code_len = len(key_single)
            code_len_count[code_len + 1] += 1
            result.append(w + "\t" + new_code + "\t2")
            code_set[new_code] = 1
            full.append("$ddcmd(" + w + "," + w + "[" + new_code + "])\t" + key_full)
        elif key_first_full not in code_set:
            code_set[key_first_full] = 1
            code_len = len(key_first_full)
            code_len_count[code_len] += 1
            result.append(w + "\t" + key_first_full + "\t3")
            full.append("$ddcmd(" + w + "," + w + "[" + key_first_full + "])\t" + key_full)
        elif key_first_full in code_set and code_set[key_first_full] < 2:
            new_code = key_first_full + padding[code_set[key_first_full]]
            code_set[key_first_full] += 1
            code_len = len(key_first_full)
            code_len_count[code_len+1] += 1
            result.append(w + "\t" + new_code + "\t4")
            code_set[new_code] = 1
            full.append("$ddcmd(" + w + "," + w + "[" + new_code + "])\t" + key_full)
        else:
            found = False
            for i in xrange(3, len(key_full)):
                code_ext = key_full[0:i]
                if code_ext not in code_set:
                    found = True
                    code_set[code_ext] = 1
                    code_len = len(code_ext)
                    code_len_count[code_len] += 1
                    result.append(w + "\t" + code_ext + "\t5")
                    code_set[new_code] = 1
                    full.append("$ddcmd(" + w + "," + w + "[" + code_ext + "])\t" + key_full)
                    break
            if not found:
                code_len = len(key_full)
                code_len_count[code_len] += 1
                if key_full in code_set:
                    code_set[key_full] += 1
                else:
                    code_set[key_full] = 1
                result.append(w + "\t" + key_full + "\t6")
        if count % 1000 == 0:
            print 'step1: processed', count, 'words'
    input_file.close()

    for line in result:
        print >> output_file, line.encode(coding)
    output_file.close()
    for line in full:
        print >> output_full_file, line.encode(coding)
    output_full_file.close()

    for line in ci:
        print >> output_ci_file, line.encode(coding)
    output_ci_file.close()


    print "Output to %s and %s" % (out_file, out_full_file)
    print "All %d words. And %d codes." % (len(result), len(code_set))
    print "Code Length Count: ", code_len_count
