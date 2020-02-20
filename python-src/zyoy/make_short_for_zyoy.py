#!/usr/bin/env python
# -*- coding: utf-8 -*-

import logging
import sys
import os

gb2312_file = open('../../../words-and-phrases-data/common-data/gb2312.txt')
print ("%s: Load gb2312 words." % sys.argv[0])
gb2312_set = set()
for line in gb2312_file:
    line = line.strip().decode("utf-8")
    if line == "" or line[0] == '#':
        continue
    gb2312_set.add(line)
gb2312_file.close()

first3000_file = open('../../../words-and-phrases-data/common-data/gb2312-1.txt')
print ("%s: Load first 3000 words." % sys.argv[0])
first_3000_set = set()
for line in first3000_file:
    line = line.strip().decode("utf-8")
    if line == "" or line[0] == '#':
        continue
    first_3000_set.add(line)
first3000_file.close()

print "%s: Load first 500 words." % sys.argv[0]
first500_file = open('../../../words-and-phrases-data/common-data/gb-first-500.txt')
first_500_set = set()
for line in first500_file:
    line = line.strip().decode("utf-8")
    if line == "" or line[0] == '#':
        continue
    first_500_set.add(line)
first500_file.close()

print "%s: Load first 1500 words." % sys.argv[0]
first1500_file = open('../../../words-and-phrases-data/common-data/gb-first-1500.txt')
first_1500_set = set()
for line in first1500_file:
    line = line.strip().decode("utf-8")
    if line == "" or line[0] == '#':
        continue
    first_1500_set.add(line)
first1500_file.close()


def sort_file(source_file, charset='utf-8'):
    """ 排序

    :param source_file: 原始文件
    :param charset:  文件编码
    :return: 
    """
    dir_name = os.path.dirname(source_file)
    temp_file = os.path.join(dir_name, 'temp.txt')
    output = os.popen('..\\sort3.py %s %s %s' % (source_file, temp_file, charset))
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

    print "%s: begin to process %s." % (sys.argv[0], infile)

    one_set = {}
    one_set[u'不'] = u'q'
    one_set[u'中'] = u'z'
    one_set[u'唔'] = u'm'
    one_set[u'发'] = u'f'

    one_set[u'的'] = u'd'
    one_set[u'同'] = u't'
    one_set[u'工'] = u'g'
    one_set[u'了'] = u'l'

    one_set[u'个'] = u'o'
    one_set[u'经'] = u'b'
    one_set[u'我'] = u'j'
    one_set[u'和'] = u'w'

    one_set[u'在'] = u'z'
    one_set[u'产'] = u'c'
    one_set[u'上'] = u's'

    one_set[u'一'] = u'y'
    one_set[u'为'] = u'w'

    one_set[u'国'] = u';'
    one_set[u'地'] = u'a'

    one_set[u'有'] = u'u'
    one_set[u'以'] = u'p'
    one_set[u'是'] = u'i'
    one_set[u'这'] = u'e'
    one_set[u'要'] = u'x'
    one_set[u'人'] = u'r'
    one_set[u'民'] = u'n'
    one_set[u'主'] = u'v'

    code_set = {}
    result = []
    full = []
    code_len_count = []
    for i in range(0, 30):
        code_len_count.append(0)

    remain = []
    ci = set()
    count = 0
    padding = ['', ';', '\'', ',', '.', '/']
    for k, v in one_set.iteritems():
        code_set[v] = 1
        code_len_count[1] += 1
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
            key_jamzit = cc[0]
            ci.add(w + '\t' + cc[0] + '\n')
            cc_full = cc[0] + cc[1]
            if w in one_set:
                pass
                result.append(w + "\t" + cc_full +"\t80")
            elif key_jamzit not in code_set:
                code_set[key_jamzit] = 1
                code_len = len(key_jamzit)
                code_len_count[code_len] += 1
                result.append(w + "\t" + key_jamzit + "\t3")
                result.append(w + "\t" + cc_full + "\t90")
            # elif code_set[key_jamzit] < 3:  # and w in first_1500_set:
            #     code_set[key_jamzit] += 1
            #     new_code = key_jamzit  # + padding[code_set[key_jamzit]]
            #     # code_set[new_code] = 1
            #     code_len = len(key_jamzit)  # + 1
            #     code_len_count[code_len] += 1
            #     result.append(w + "\t" + new_code + "\t4\n")
            #
            #     full.append("$ddcmd(" + w + "," + w + "[" + key_jamzit + "])\t" + cc_full)
            #     # count -= 1
            #     # remain.append(line)
            else:
                count -= 1
                remain.append(line)
        except Exception as err:
            logging.error(err)
    print '%s: processed %s words' % (sys.argv[0], count)
    input_file.close()
    print ("%s: %d words. And %d codes." % (sys.argv[0], len(result), len(code_set)))
    print("%s: process remain..." % sys.argv[0])
    for line in remain:
        # print(line)
        count += 1
        try:
            kv = line.split(None)
            w = kv[0]
            c = kv[1]
            cc = c.split(':')
            added = False
            cc_full = cc[0] + cc[1]
            for padding in range(1, len(cc[1]) + 1):
                key_with_ext = cc[0] + cc[1][0:padding]
                code_len = len(key_with_ext)
                # if code_len > 4:
                #     break
                if key_with_ext not in code_set:
                    code_set[key_with_ext] = 1
                    code_len_count[code_len] += 1
                    result.append(w + "\t" + key_with_ext + "\t5")
                    if key_with_ext != cc[0] + cc[1]:
                        if w in first_1500_set:
                            result.append(w + "\t" + cc_full + "\t100")
                        else:
                            full.append("$ddcmd(" + w + "," + w + "[" + key_with_ext + "])\t" + cc_full)
                    added = True
                    break
            if not added:
                code = cc[0] + cc[1]
                if w in gb2312_set:
                    code_set[code] = 1
                    code_len_count[len(code)] += 1
                    result.append(w + "\t" + code + "\t6")
                else:
                    full.append(w + "\t" + code + "\t100")
        except Exception as err:
            logging.error(err)
        if count % 1000 == 0:
            print '%s: processed %s words' % (sys.argv[0], count)

    print '%s: processed %s words' % (sys.argv[0], count)
    print "%s: result: %d" % (sys.argv[0], len(result))

    for line in result:
        print >> output_file, line.encode(coding)
    output_file.close()

    for line in full:
        print >> output_full_file, line.encode(coding)
    output_full_file.close()

    for line in ci:
        print >> output_ci_file, line.encode(coding)
    output_ci_file.close()

    print "%s: Output to %s and %s" % (sys.argv[0], out_file, out_full_file)
    print "%s: All %d words. And %d codes." % (sys.argv[0], len(result), len(code_set))
    print "%s: Code Length Count: %s" % (sys.argv[0], code_len_count)


def main():
    infile = '../../out/zyoy.single.merged.txt'
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
