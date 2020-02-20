#!/usr/bin/env python
# -*- coding: utf-8 -*-

import logging
import sys
import os

def load_common_words():
    print "%s: Load common words." % sys.argv[0]
    common_file = open('data/common-words.txt')
    common_set = set()
    for line in common_file:
        line = line.strip().decode("utf-8")
        if line == "" or line[0] == '#':
            continue
        common_set.add(line)
    common_file.close()
    print "common words loaded."
    return common_set


def load_one_set():
    """ 加载固定简码表
    """
    print "loading one_set ..."
    one_set = {}
    f = open("data/1_2.txt")
    for line in f:
        line = line.strip().decode("utf-8")
        if line == u"" or line[0] == u"#":
            continue
        word, code = line.split()
        one_set[word] = code
    print "one_set loaded."
    return one_set

def load_last_stroke():
    """ 加载笔画表
    """
    print "loading one_set ..."
    last_set = {}
    f = open("data/bh-last.txt")
    for line in f:
        line = line.strip().decode("utf-8")
        if line == u"" or line[0] == u"#":
            continue
        word, code = line.split()
        last_set[word] = code
    print "last_set loaded."
    return last_set

def sort_file(source_file, charset='utf-8'):
    """ 排序

    :param source_file: 原始文件
    :param charset:  文件编码
    :return: nil
    """
    dir_name = os.path.dirname(source_file)
    temp_file = os.path.join(dir_name, 'temp.txt')
    output = os.popen('sort3.py %s %s %s' % (source_file, temp_file, charset))
    print output.read()
    import shutil
    shutil.move(temp_file, source_file)
    # open(source_file, "wb").write(open(temp_file, "rb").read())
    # os.remove(temp_file)


def make_short(infile, coding='utf-8'):
    (short_name, extension) = os.path.splitext(infile)
    out_file = '%s-out%s' % (short_name, extension)
    out_full_file = '%s-full%s' % (short_name, extension)
    out_ci_file = '%s-ci%s' % (short_name, extension)

    input_file = open(infile)
    output_file = open(out_file, "w")
    output_full_file = open(out_full_file, "w")
    output_ci_file = open(out_ci_file, "w")
    common_set  = load_common_words()
    one_set = load_one_set()
    last_set = load_last_stroke()

    print "%s: begin to process %s." % (sys.argv[0], infile)

    code_set = {}
    result = []
    full = []
    sp = []
    code_len_count = []
    for i in range(0, 30):
        code_len_count.append(0)

    remain = {}
    ci = set()
    count = 0
    padding = ['', ';', '\'', ',', '.', '/']
    for k, v in one_set.iteritems():
        if v in code_set:
            code_set[v] += 1
        else:
            code_set[v] = 1
        code_len_count[len(v)] += 1
        result.append(k + "\t" + v + "\t1")

    for line in input_file:
        count += 1
        line = line.strip().decode(coding)
        if line == "" or line[0] == '#':
            continue
        try:
            kv = line.split(None)
            w = kv[0]
            c = kv[1]
            cc = c.split(':')
            key_single = cc[0][0]
            key_syllable = cc[0]
            key_ext = cc[0] + cc[1][0]
            ci.add(w + '\t' + cc[0] + '\n')
            cc_full = cc[0] + cc[1]
            if w in one_set:
                full.append(w + "\t" + cc_full)

            elif key_single not in code_set:
                code_set[key_single] = 1
                code_len = len(key_single)
                code_len_count[code_len] += 1
                result.append(w + "\t" + key_single + "\t1")
                if w in common_set:
                    full.append(w + "\t" + cc_full)
                else:
                    sp.append(w + "\t" + cc_full)
            elif key_single in code_set and code_set[key_single] < 1:
                code_set[key_single] += 1
                code_len = len(key_single)
                code_len_count[code_len] += 1
                result.append(w + "\t" + key_single + "\t2")
                if w in common_set:
                    full.append(w + "\t" + cc_full)
                else:
                    sp.append(w + "\t" + cc_full)
            elif key_syllable not in code_set:
                code_set[key_syllable] = 1
                code_len = len(key_syllable)
                code_len_count[code_len] += 1
                result.append(w + "\t" + key_syllable + "\t3")
                if w in common_set:
                    full.append(w + "\t" + cc_full)
                else:
                    sp.append(w + "\t" + cc_full)
            elif key_syllable in code_set \
                    and w in common_set \
                    and w in last_set \
                    and code_set[key_single] < 5 :
                
                key_syllable_ext = key_syllable + last_set[w]
                if key_syllable_ext not in code_set:
                    code_set[key_syllable_ext] = 1
                    code_set[key_syllable] = 1
                    code_len = len(key_syllable)
                    code_len_count[code_len] += 1
                    result.append(w + "\t" + key_syllable_ext + "\t4")
                    full.append(w + "\t" + cc_full)
                    print "here",w,key_syllable_ext
                else:
                    if cc_full in remain:
                        remain[cc_full].append(w)
                    else:
                        remain[cc_full] = [w]
            #elif key_ext not in code_set:
            #    code_set[key_ext] = 1
            #    code_len = len(key_ext)
            #    code_len_count[code_len] += 1
            #    result.append(w + "\t" + key_ext + "\t4")
            #    if w in common_set:
            #        full.append(w + "\t" + cc_full)
            #    else:
            #        sp.append(w + "\t" + cc_full)
            else:
               if cc_full in remain:
                    remain[cc_full].append(w)
               else:
                    remain[cc_full] = [w]
        except Exception as err:
            logging.error(err)

    print '%s: processed %s words' % (sys.argv[0], count)
    input_file.close()
    print ("%s: %d words. And %d codes." % (sys.argv[0], len(result), len(code_set)))
    print("%s: process remain..." % sys.argv[0])
    for k, v in remain.iteritems():
        ws_len = len(v)
        w = v[0]
        if ws_len == 1:
            if k in code_set:
                code_set[k] += 1
            else:
                code_set[k] = 1
            code_len_count[len(k)] += 1
            if w in common_set:
                result.append(w + "\t" + k + "\t6")
            else:
                result.append(w + "\t" + k + "\t7")
        else:
            code_three = k[0:3]
            if code_three not in code_set:
                code_set[code_three] = 1
                code_len_count[3] += 1
                result.append(w + "\t" + code_three + "\t5")
                if w in common_set:
                    full.append(w + "\t" + k)
                else:
                    sp.append(w + "\t" + k)
            else:
                if k not in code_set:
                    code_set[k] = 1
                    code_len_count[len(k)] += 1
                    if w in common_set:
                        result.append(w + "\t" + k + "\t6")
                    else:
                        result.append(w + "\t" + k + "\t7")
                else:
                    code_set[k] += 1
                    code_len_count[len(k)] += 1
                    if w in common_set:
                        result.append(w + "\t" + k + "\t8")
                    else:
                        sp.append(w + "\t" + k)

            for i in xrange(1, ws_len):
                w = v[i]
                if k in code_set:
                    if w in common_set:
                        code_set[k] += 1
                        code_len_count[len(k)] += 1
                        result.append(w + "\t" + k + "\t8")
                    else:
                        sp.append(w + "\t" + k)
                    # code_set[k] += 1
                    # result.append(w + "\t" + k + "\t7")
                    # code_len_count[len(k)] += 1
                else:
                    code_set[k] = 1
                    if w in common_set:
                        result.append(w + "\t" + k + "\t6")
                    else:
                        result.append(w + "\t" + k + "\t7")
                    code_len_count[len(k)] += 1

    print '%s: processed %s words' % (sys.argv[0], count)
    print "%s: result: %d" % (sys.argv[0], len(result))

    for line in full:
        # print >> output_full_file, line.encode(coding)
        kv = line.split("\t")
        if kv[1] not in code_set:
            code_set[kv[1]] = 1
            code_len_count[len(kv[1])] += 1
            result.append(kv[0] + "\t" + kv[1] + "\t20");
        else:
            print >> output_full_file, line.encode(coding)

    for line in sp:
        kv = line.split("\t")
        if kv[1] not in code_set:
            code_set[kv[1]] = 1
            code_len_count[len(kv[1])] += 1
            result.append(kv[0] + "\t" + kv[1] + "\t30");
        else:
           print >> output_full_file, line.encode(coding)
    output_full_file.close()
    
    for line in result:
        print >> output_file, line.encode(coding)
    output_file.close()

    for line in ci:
        print >> output_ci_file, line.encode(coding)
    output_ci_file.close()

    print "%s: Output to %s and %s" % (sys.argv[0], out_file, out_full_file)
    print "%s: All %d words. And %d codes." % (sys.argv[0], len(result), len(code_set))
    print "%s: Code Length Count: %s" % (sys.argv[0], code_len_count)


def main():
    infile = 'out/zyoy.single.merged.txt'
    encoding = 'utf-8'
    length = len(sys.argv)
    if length > 2:
        infile = sys.argv[1]
        encoding = sys.argv[2]
    elif length > 1:
        infile = sys.argv[1]

    make_short(infile, encoding)
    print('%s: DONE!' % sys.argv[0])


if __name__ == '__main__':
    main()
