#!/usr/bin/env python
# -*- coding: utf-8 -*-

import logging
import sys
import os

logging.basicConfig(level=logging.DEBUG,
                    format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s %(message)s',
                    datefmt='%a, %d %b %Y %H:%M:%S',
                    filename='..\\out\\myapp.log',
                    filemode='w')

def make_short(infile, coding='utf-8'):
    base_name = os.path.basename(infile)
    (short_name, extension) = os.path.splitext(base_name)
    out_file = '..\\..\\out\\%s-out%s' % (short_name, extension)
    out_full_file = '..\\..\\out\\%s-full%s' % (short_name, extension)
    out_ci_file = '..\\..\\out\\%s-ci%s' % (short_name, extension)

    first3000_file = '../../../words-and-phrases-data/common-data/gb2312-1.txt')
    input_file = open(infile)
    output_file = open(out_file, "w")
    output_full_file = open(out_full_file, "w")
    output_ci_file = open(out_ci_file, "w")

    code_set = {}
    result = []
    full = []
    code_len_count = []
    for i in range(0, 30):
        code_len_count.append(0)
    ci = set()

    padding = ['', '', '', '', '']
    # padding = ['', ';', '/', ',', '.']

    print "%s: Load first 3000 words." % sys.argv[0]
    first_3000_set = set()
    for line in first3000_file:
        line = line.strip().decode(coding)
        if line == "" or line[0] == '#':
            continue
        first_3000_set.add(line)
    first3000_file.close()

    # code one
    words_has_short = []
    single_file = "../../../words-and-phrases-data/cantonese-data/short-single-t.txt"
    for line in single_file:
        line = line.strip().decode('utf-8')
        if line == '' or line[0] == '#':
            continue
        kv = line.split(None)
        code_set[kv[1]] = 1
        words_has_short.append(kv[0])
        code_len_count[1] += 1
        result.append(line)
    single_file.close()

    print "Begin to process ", infile
    count = 0
    ext_count = 0
    for line in input_file:
        count += 1
        line = line.decode(coding).strip()
        if line == "" or line[0] == '#':
            continue
        try:
            kv = line.split(None)
            w = kv[0]
            c = kv[1]
            cc = c.split(':')
            key_single = cc[0][0]
            key_yamzit = cc[0][0:-1]
            key_yamzit_full = cc[0]
            key_yamzit_ext = cc[0] + cc[1][0]
            key_full = cc[0] + cc[1]
            continue_flag = False
            if w in words_has_short:
                full.append(w + "\t" + key_full)
                continue_flag = True
            if continue_flag:
                continue
            if key_single not in code_set:
                words_has_short.append(w)
                code_set[key_single] = 1
                code_len = len(key_single)
                code_len_count[code_len] += 1
                result.append(w + "\t" + key_single + "\t1")
                full.append(w + "\t" + key_full)
            elif code_set[key_single] < 5 and w in first_3000_set:  # and w in first_3000_set
                words_has_short.append(w)
                new_code = key_single + padding[code_set[key_single]]
                code_set[key_single] += 1
                code_len = len(new_code)
                code_len_count[code_len] += 1
                result.append(w + "\t" + new_code + "\t2")
                # if new_code in code_set:
                #     code_set[new_code] += 1
                # else:
                #     code_set[new_code] = 1
                full.append(w + "\t" + key_full)
            elif key_yamzit not in code_set:
                words_has_short.append(w)
                code_set[key_yamzit] = 1
                code_len = len(key_yamzit)
                code_len_count[code_len] += 1
                result.append(w + "\t" + key_yamzit + "\t3")
                full.append(w + "\t" + key_full)
            elif code_set[key_yamzit] < 5 and w in first_3000_set:  # and w in first_3000_set
                words_has_short.append(w)
                new_code = key_yamzit + padding[code_set[key_yamzit]]
                code_set[key_yamzit] += 1
                code_len = len(new_code) + 1
                code_len_count[code_len] += 1
                result.append(w + "\t" + new_code + "\t4")
                # if new_code in code_set:
                #     code_set[new_code] += 1
                # else:
                #     code_set[new_code] = 1
                full.append(w + "\t" + cc[0] + cc[1])
            elif key_yamzit_full not in code_set:
                words_has_short.append(w)
                code_set[key_yamzit_full] = 1
                code_len = len(key_yamzit_full)
                code_len_count[code_len] += 1
                result.append(w + "\t" + key_yamzit_full + "\t5")
                full.append(w + "\t" + key_full)
            elif code_set[key_yamzit_full] < 5 and w in first_3000_set:  # and w in first_3000_set
                words_has_short.append(w)
                new_code = key_yamzit_full + padding[code_set[key_yamzit_full]]
                code_set[key_yamzit_full] += 1
                code_len = len(new_code) + 1
                code_len_count[code_len] += 1
                result.append(w + "\t" + new_code + "\t6")
                # if new_code in code_set:
                #     code_set[new_code] += 1
                # else:
                #     code_set[new_code] = 1
                full.append(w + "\t" + cc[0] + cc[1])
            elif key_yamzit_ext not in code_set:
                ext_count += 1
                words_has_short.append(w)
                code_set[key_yamzit_ext] = 1
                code_len = len(key_yamzit_ext)
                code_len_count[code_len] += 1
                result.append(w + "\t" + key_yamzit_ext + "\t7")
                full.append(w + "\t" + key_full)
            else:
                ext_count += 1
                if key_full in code_set:
                    code_set[key_full] += 1
                else:
                    code_set[key_full] = 1
                code_len = len(key_full)
                code_len_count[code_len] += 1
                result.append(w + "\t" + key_full + "\t8")
        except Exception as err:
            logging.error(err)
        if count % 100 == 0:
            print 'processed', count, 'words'
    input_file.close()

    print 'Processed', count, 'words.'
    print 'Has', ext_count, 'ext words.'
    print "Result: %d" % len(result)

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


def main():
    infile = '../../out/zyoy.single.merged.txt'
    # infile = '../data/cantonese.9-tones-canon.txt'
    encoding = 'utf-8'
    length = len(sys.argv)
    if length > 2:
        infile = sys.argv[1]
        encoding = sys.argv[2]
    elif length > 1:
        infile = sys.argv[1]

    make_short(infile, encoding)
    print('DONE!')


if __name__ == '__main__':
    main()
