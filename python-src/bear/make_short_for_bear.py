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

print ("%s: Load First-Stroke..." % sys.argv[0])
first_stroke_file = open(find_file('../../../words-and-phrases-data/bihua-data/bh-first.txt'))
first_stroke_set = {}
for line in first_stroke_file:
    line = line.strip().decode("utf-8")
    if line == "" or line[0] == '#':
        continue
    kv = line.split()
    first_stroke_set[kv[0]] = kv[1]
first_stroke_file.close()

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


def get_padding_by_word(word):
    padding_map = {'': '', 's': ';', 'p': ',', 'd': '.', 'z': '.', 'w': '/'}
    if word in first_stroke_set:
        k = first_stroke_set[word]
        if k in padding_map:
            return padding_map[k]
        else:
            return k, '没有这种笔画'
    else:
        print word, '没有定义笔画'


def get_padding_by_order(order):
    # padding = ['', '', '', '', '', '', '', '', '', '', '', '']
    padding = [u'', u';', u'/', u',', u'.']
    return padding[order]


def make_short(infile, coding='utf-8'):
    (short_name, extension) = os.path.splitext(infile)
    out_file = '%s-out%s' % (short_name, extension)
    out_full_file = '%s-full%s' % (short_name, extension)
    out_ci_file = '%s-ci%s' % (short_name, extension)
    input_file = open(infile)
    output_file = open(out_file, "w")
    output_full_file = open(out_full_file, "w")
    output_ci_file = open(out_ci_file, "w")

    print ("%s: Begin to process %s" % (sys.argv[0], infile))
    code_set = {}
    result = []
    full = []
    code_len_count = []
    z1500 = []
    z500 = []
    for i in range(0, 30):
        code_len_count.append(0)
        z1500.append(0)
        z500.append(0)
    remain = []
    ci = set()
    count = 0
    one_set = {}
    one_set[u'不'] = u'b'
    one_set[u'地'] = u'p'
    one_set[u'民'] = u'm'
    one_set[u'发'] = u'f'

    one_set[u'的'] = u'd'
    one_set[u'同'] = u't'
    one_set[u'中'] = u'n'
    one_set[u'了'] = u'l'

    one_set[u'个'] = u'g'
    one_set[u'上'] = u'k'
    one_set[u'和'] = u'h'
    one_set[u'我'] = u'r'

    one_set[u'在'] = u'z'
    one_set[u'产'] = u'c'
    one_set[u'是'] = u's'

    one_set[u'人'] = u'y'
    one_set[u'为'] = u'w'

    one_set[u'国'] = u'o'
    one_set[u'要'] = u'q'
    one_set[u'以'] = u'i'
    one_set[u'经'] = u'j'
    one_set[u'有'] = u'e'
    one_set[u'一'] = u'a'
    one_set[u'主'] = u'v'
    one_set[u'工'] = u'u'
    #one_set[u'这'] = u'e'
    one_set[u'唔'] = u'x'

    # one_set[u'发'] = u'a'
    # one_set[u'的'] = u'b'
    # one_set[u'民'] = u'c'
    # one_set[u'为'] = u'd'
    # one_set[u'工'] = u'e'
    # one_set[u'中'] = u'f'
    # one_set[u'主'] = u'g'
    # one_set[u'一'] = u'h'
    # one_set[u'这'] = u'i'
    # one_set[u'我'] = u'j'
    # one_set[u'地'] = u'k'
    # one_set[u'以'] = u'l'
    # one_set[u'个'] = u'm'
    # one_set[u'同'] = u'n'
    # one_set[u'唔'] = u'o'
    # one_set[u'产'] = u'p'
    # one_set[u'要'] = u'q'
    # one_set[u'有'] = u'r'
    # one_set[u'经'] = u's'
    # one_set[u'不'] = u't'
    # one_set[u'和'] = u'u'
    # one_set[u'人'] = u'v'
    # one_set[u'了'] = u'w'
    # one_set[u'在'] = u'x'
    # one_set[u'上'] = u'y'
    # one_set[u'是'] = u'z'
    # one_set[u'国'] = u';'

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
            key_two = cc[0][0:2]
            key_three = cc[0]
            key_full = cc[0] + cc[1]
            ci.add(w + '\t' + cc[0])
            if w in one_set:
                full.append("$ddcmd(" + w + "," + w + "[" + one_set[w] + "])\t" + key_full)
                if w in first_500_set:
                    z500[1] += 1
                elif w in first_1500_set:
                    z1500[1] += 1
            # if key_single not in code_set:
            #     code_set[key_single] = 1
            #     code_len = len(key_single)
            #     code_len_count[code_len] += 1
            #     result.append(w + "\t" + key_single + "\t1")
            #     full.append(w + "\t" + key_full)
            #     if w in first_500_set:
            #         z500[1] += 1
            #     elif w in first_1500_set:
            #         z1500[1] += 1

            # elif code_set[key_single] < 2:
            #     new_code = key_single + get_padding_by_order(code_set[key_single]) # get_padding_by_word(w)  #
            #     code_set[key_single] += 1
            #     code_set[new_code] = 1
            #     code_len = len(new_code)
            #     code_len_count[code_len] += 1
            #     result.append(w + "\t" + new_code + "\t2")
            #     full.append(w + "\t" + key_full)
            elif key_two not in code_set:
                code_set[key_two] = 1
                code_len = len(key_two)
                code_len_count[code_len] += 1
                result.append(w + "\t" + key_two + "\t3")
                full.append("$ddcmd(" + w + "," + w + "[" + key_two + "])\t" + key_full)
                if w in first_500_set:
                    z500[code_len] += 1
                elif w in first_1500_set:
                    z1500[code_len] += 1
            elif code_set[key_two] < 3 and w in first_1500_set:
                new_code = key_two + get_padding_by_order([code_set[key_two]])
                code_set[key_two] += 1
                code_set[new_code] = 1
                code_len_count[3] += 1
                result.append(w + "\t" + new_code + "\t3")
                full.append("$ddcmd(" + w + "," + w + "[" + new_code + "])\t" + key_full)
            elif key_three not in code_set:
                code_set[key_three] = 1
                code_len = len(key_three)
                code_len_count[code_len] += 1
                result.append(w + "\t" + key_three + "\t3")
                full.append(w + "\t" + key_full)
                if w in first_500_set:
                    z500[code_len] += 1
                elif w in first_1500_set:
                    z1500[code_len] += 1
            # elif code_set[key_two] < 2:
            #     new_code = key_two + get_padding_by_order(code_set[key_two]) # get_padding_by_word(w)  #
            #     code_set[key_two] += 1
            #     code_set[new_code] = 1
            #     code_len = len(new_code)
            #     code_len_count[code_len] += 1
            #     result.append(w + "\t" + new_code + "\t4")
            #     full.append(w + "\t" + key_full)
            else:
                count -= 1
                remain.append(line)
        except Exception as err:
            # logging.error(err)
            print "err:", err
        if count % 1000 == 0:
            print ('%s: processed %d words.' % (sys.argv[0], count))

    print '%s: processed %d words.' % (sys.argv[0], count)
    input_file.close()
    print "%s: %d words. And %d codes." % (sys.argv[0], len(result), len(code_set))


    print 'processing remain  %d ...' % len(remain)
    for line in remain:
        try:
            kv = line.split(None)
            w = kv[0]
            c = kv[1]
            cc = c.split(':')
            found = False
            for i in xrange(1, len(cc[0][0])):
                key_with_ext = cc[1] + cc[0][0:i]
                if key_with_ext not in code_set:
                    code_set[key_with_ext] = 1
                    code_len = len(key_with_ext)
                    code_len_count[code_len] += 1
                    if w in first_3000_set:
                        result.append(w + "\t" + key_with_ext + "\t5")
                    else:
                        result.append(w + "\t" + key_with_ext + "\t7")
                    found = True
                    full.append(w + "\t" + key_full)
                    if w in first_500_set:
                        z500[code_len] += 1
                        # if code_len > 3:
                        #     print w, key_with_ext
                    elif w in first_1500_set:
                        z1500[code_len] += 1
                    break
            if not found:
                key_with_ext = cc[1] + cc[0][0]
                code_len = len(key_with_ext)
                code_len_count[code_len] += 1
                if w in first_3000_set:
                    result.append(w + "\t" +key_with_ext + "\t6")
                else:
                    result.append(w + "\t" + key_with_ext + "\t7")
                if w in first_500_set:
                    z500[code_len] += 1
                    # if code_len > 3:
                    #     print w, key_with_ext
                elif w in first_1500_set:
                    z1500[code_len] += 1
                if key_with_ext in code_set:
                    code_set[key_with_ext] += 1
                    print key_with_ext, w, code_set[key_with_ext]
                else:
                    code_set[key_with_ext] = 1
        except Exception as err:
            logging.error(err)
        if count % 1000 == 0:
            print ('%s: processed %d words.' % (sys.argv[0], count))

    print ('%s: processed %d words.' % (sys.argv[0], count))
    print ("%s: result: %d" % (sys.argv[0], len(result)))

    for line in result:
        print >> output_file, line.encode(coding)
    output_file.close()
    for line in full:
        print >> output_full_file, line.encode(coding)
    output_full_file.close()

    for line in ci:
        print >> output_ci_file, line.encode(coding)
    output_ci_file.close()

    print ("%s: Output to %s and %s" % (sys.argv[0], out_file, out_full_file))
    print ("%s: All %d words. And %d codes." % (sys.argv[0], len(result), len(code_set)))
    print ("%s: Code Length Count: %s." % (sys.argv[0], code_len_count))
    print ("%s: Z_500 Count: %s." % (sys.argv[0], z500))
    print ("%s: Z_1500 Count: %s." % (sys.argv[0], z1500))

def main():
    infile = r'../../out/merged.txt'
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
