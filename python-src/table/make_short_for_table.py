#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys, os


han_l1_file = open('../../../wwords-and-phrases-data/common-data/han-level-1.txt')
print ("%s: Load han-level-1 words." % sys.argv[0])
han_l1_set = set()
for line in han_l1_file:
    line = line.strip().decode("utf-8")
    if line == "" or line[0] == '#':
        continue
    han_l1_set.add(line)
han_l1_file.close()

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
    out_other_file = '%s-other%s' % (short_name, extension)

    input_file = open(infile)
    output_file = open(out_file, "w")
    output_full_file = open(out_full_file, "w")
    output_ci_file = open(out_ci_file, "w")
    output_other_file = open(out_other_file, "w")

    print "%s: Begin to process %s" % (sys.argv[0], infile)

    code_set = {}
    result = []
    full = []
    ci = []
    sp = []
    remain = {}
    code_len_count = []

    from one_set import one_set

    for i in range(0, 30):
        code_len_count.append(0)

    count = 0
    padding = ['', ';', ',', '.', '/', '6', '7', '8', '9', '0']
    # padding = ['', ';']
    for k, v in one_set.iteritems():
        code_set[v] = 1
        code_len_count[len(v)] += 1
        result.append(k + "\t" + v + "\t1")

    for line in input_file:
        line = line.strip().decode(coding)
        if line == "" or line[0] == '#':
            continue
        try:
            kv = line.split(None)
            w = kv[0]
            c = kv[1]
            cc = c.split(":")
            cc_full = cc[0] + cc[1]
            ci.append(w + '\t' + cc_full)
            # if w not in gb2312_set:
            #     continue
            count += 1

            code_one = cc_full[0]
            code_two = cc_full[0:2]
            code_three = cc_full[0:3]
            if w in one_set:
                #full.append("$ddcmd(" + w + "," + w + "[" + one_set[w] + "])\t" + cc_full)
                full.append(w + "\t" + cc_full)
            elif code_set[code_one] < 2 and w in first_500_set:
                new_code = code_one + padding[code_set[code_one]]
                code_set[code_one] += 1
                code_set[new_code] = 1
                code_len_count[2] += 1
                result.append(w + "\t" + new_code + "\t1")
                if w in first_3000_set:
                    #full.append("$ddcmd(" + w + "," + w + "[" + new_code + "])\t" + cc_full)
                    full.append(w + "\t" + cc_full)
                else:
                    #sp.append("$ddcmd(" + w + "," + w + "[" + new_code + "])\t" + cc_full)
                    sp.append(w + "\t" + cc_full)
            elif code_two not in code_set:
                code_set[code_two] = 1
                code_len_count[2] += 1
                result.append(w + "\t" + code_two + "\t2")
                if w in first_3000_set:
                    #full.append("$ddcmd(" + w + "," + w + "[" + code_two + "])\t" + cc_full)
                    full.append(w + "\t" + cc_full)
                else:
                    #sp.append("$ddcmd(" + w + "," + w + "[" + code_two + "])\t" + cc_full)
                    sp.append(w + "\t" + cc_full)
            elif code_set[code_two] < 5 and w in first_500_set:
                new_code = code_two + padding[code_set[code_two]]
                code_set[code_two] += 1
                code_set[new_code] = 1
                code_len_count[3] += 1
                result.append(w + "\t" + new_code + "\t2")
                if w in first_3000_set:
                    #full.append("$ddcmd(" + w + "," + w + "[" + new_code + "])\t" + cc_full)
                    full.append(w + "\t" + cc_full)
                else:
                    #sp.append("$ddcmd(" + w + "," + w + "[" + new_code + "])\t" + cc_full)
                    sp.append(w + "\t" + cc_full)
            elif code_three not in code_set:  # and w in han_l1_set:
                code_set[code_three] = 1
                code_len_count[3] += 1
                result.append(w + "\t" + code_three + "\t3")
                # result.append(w + "\t" + cc_full + "\t7")
                # if cc_full in code_set:
                #     code_set[cc_full] += 1
                # else:
                #     code_set[cc_full] = 1
                if w in first_3000_set:
                    #full.append("$ddcmd(" + w + "," + w + "[" + code_three + "])\t" + cc_full)
                    full.append(w + "\t" + cc_full)
                else:
                    #sp.append("$ddcmd(" + w + "," + w + "[" + code_three + "])\t" + cc_full)
                    sp.append(w + "\t" + cc_full)
            # elif code_set[code_three] < 2:  # and w in first_3000_set:
            #     new_code = code_three + padding[code_set[code_three]]
            #     code_set[code_three] += 1
            #     code_set[new_code] = 1
            #     code_len_count[4] += 1
            #     result.append(w + "\t" + new_code + "\t4")
            #     full.append("$ddcmd(" + w + "," + w + "[" + new_code + "])\t" + cc_full)
            else:
                if cc_full in remain:
                    remain[cc_full].append(w)
                else:
                    remain[cc_full] = [w]
        except Exception as err:
            print __file__, line.encode("utf8"), err

    input_file.close()

    remain2 = {}
    for k, v in remain.iteritems():
        ws_len = len(v)
        ls = []
        for i in xrange(0, ws_len):
            w = v[i]
            if w not in first_3000_set:
                ls.append(w)
                continue
            code_three = k[0:3]
            if code_three not in code_set:
                code_set[code_three] = 1
                code_len_count[3] += 1
                result.append(w + "\t" + code_three + "\t3")
                full.append(w + "\t" + k)
                # if w in han_l1_set:
                #     #full.append("$ddcmd(" + w + "," + w + "[" + code_three + "])\t" + k)
                # else:
                #     #sp.append("$ddcmd(" + w + "," + w + "[" + code_three + "])\t" + k)
            elif code_set[code_three] < 1:
                new_code = code_three + padding[code_set[code_three]]
                code_set[code_three] += 1
                code_set[new_code] = 1
                code_len_count[4] += 1
                result.append(w + "\t" + new_code + "\t4")
                #full.append("$ddcmd(" + w + "," + w + "[" + new_code + "])\t" + k)
                full.append(w + "\t" + k)
        if len(ls) > 0:
            remain2[k] = ls

    remain3 = {}
    for k, v in remain2.iteritems():
        ws_len = len(v)
        ls = []
        for i in xrange(0, ws_len):
            w = v[i]
            code_three = k[0:3]
            if code_three not in code_set:
                code_set[code_three] = 1
                code_len_count[3] += 1
                result.append(w + "\t" + code_three + "\t3")
                if w in han_l1_set:
                    # full.append("$ddcmd(" + w + "," + w + "[" + code_three + "])\t" + k)
                    full.append(w + "\t" + k)
                else:
                    # sp.append("$ddcmd(" + w + "," + w + "[" + code_three + "])\t" + k)
                    sp.append(w + "\t" + k)
            elif code_set[code_three] < 1:
                new_code = code_three + padding[code_set[code_three]]
                code_set[code_three] += 1
                code_set[new_code] = 1
                code_len_count[4] += 1
                result.append(w + "\t" + new_code + "\t4")
                if w in han_l1_set:
                    # full.append("$ddcmd(" + w + "," + w + "[" + code_three + "])\t" + k)
                    full.append(w + "\t" + k)
                else:
                    # sp.append("$ddcmd(" + w + "," + w + "[" + code_three + "])\t" + k)
                    sp.append(w + "\t" + k)
            else:
                ls.append(w)
        if len(ls) > 0:
            remain3[k] = ls

    for k, v in remain3.iteritems():
        ws_len = len(v)
        w = v[0]
        if ws_len == 1:
            code_three = k[0:3]
            if code_three not in code_set:
                code_set[code_three] = 1
                code_len_count[3] += 1
                result.append(w + "\t" + code_three + "\t5")
                if w in han_l1_set:
                    #full.append("$ddcmd(" + w + "," + w + "[" + code_three + "])\t" + k)
                    full.append(w + "\t" + k)
                else:
                    #sp.append("$ddcmd(" + w + "," + w + "[" + code_three + "])\t" + k)
                    sp.append(w + "\t" + k)
            else:
                if k in code_set:
                    code_set[k] += 1
                else:
                    code_set[k] = 1
                code_len_count[len(k)] += 1
                if w in han_l1_set:
                    result.append(w + "\t" + k + "\t6")
                else:
                    result.append(w + "\t" + k + "\t7")
        else:
            code_three = k[0:3]
            if code_three not in code_set:
                code_set[code_three] = 1
                code_len_count[3] += 1
                result.append(w + "\t" + code_three + "\t5")

                if w in han_l1_set:
                    #full.append("$ddcmd(" + w + "," + w + "[" + code_three + "])\t" + k)
                    full.append(w + "\t" + k)
                else:
                    #sp.append("$ddcmd(" + w + "," + w + "[" + code_three + "])\t" + k)
                    sp.append(w + "\t" + k)
            else:
                if k not in code_set:
                    code_set[k] = 1
                    code_len_count[len(k)] += 1
                    result.append(w + "\t" + k + "\t6")
                else:
                    # if w in gb2312_set:
                    #     code_set[k] += 1
                    #     code_len_count[len(k)] += 1
                    #     result.append(w + "\t" + k + "\t7")
                    # else:
                    #     full.append(w + "\t" + k)

                    code_set[k] += 1
                    code_len_count[len(k)] += 1
                    if w in han_l1_set:
                        result.append(w + "\t" + k + "\t8")
                    else:
                        sp.append(w + "\t" + k)

            for i in xrange(1, ws_len):
                w = v[i]
                if k in code_set:
                    if w in han_l1_set:
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
                    result.append(w + "\t" + k + "\t6")
                    code_len_count[len(k)] += 1

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
        # print >> output_other_file, line.encode(coding)
        kv = line.split("\t")
        if kv[1] not in code_set:
            code_set[kv[1]] = 1
            code_len_count[len(kv[1])] += 1
            result.append(kv[0] + "\t" + kv[1] + "\t30");
        else:
           print >> output_other_file, line.encode(coding)
    # print(len(result), result)
    for line in result:
        print >> output_file, line.encode(coding)
    output_file.close()

    # for line in full:
    #     print >> output_full_file, line.encode(coding)
    output_full_file.close()

    for line in ci:
        print >> output_ci_file, line.encode(coding)
    output_ci_file.close()

    # for line in sp:
    #     print >> output_other_file, line.encode(coding)
    output_other_file.close()

    print '%s: processed %d words.' % (sys.argv[0], count)
    print "%s: %d words, and %d codes." % (sys.argv[0], len(result), len(code_set))
    print "%s: result: %d" % (sys.argv[0], len(result))
    print "%s: Output to %s." % (sys.argv[0], out_file)
    print "%s: All %d words, and %d codes." % (sys.argv[0], len(result), len(code_set))
    print "%s: Code length count: %s." % (sys.argv[0], code_len_count)


def main():
    infile = r'../out/zyoy.single.merged.txt'
    encoding = 'utf-8'
    length = len(sys.argv)
    if length > 2:
        infile = sys.argv[1]
        encoding = sys.argv[2]
    elif length > 1:
        infile = sys.argv[1]

    make_short(infile, encoding)

    sort_file('../../out/zyoy.single.merged-out.txt')
    sort_file('../../out/zyoy.single.merged-full.txt')
    sort_file('../../out/zyoy.single.merged-ci.txt')

    print('%s: DONE!' % sys.argv[0])


if __name__ == '__main__':
    main()
