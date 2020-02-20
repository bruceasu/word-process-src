#!/usr/bin/env python
# -*- coding: utf-8 -*-

import logging
import sys
import os

def load_common_set():
    print "%s: Load common words." % sys.argv[0]
    common_file = open('data\\han-level-1.s.txt')
    common_set = set()
    for line in common_file:
        line = line.strip().decode("utf-8")
        if line == "" or line[0] == '#':
            continue
        common_set.add(line)
    common_file.close()
    return common_set

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

class DataContext:
    def __init__(self):
        self.coding = 'utf-8'
        self.result = []
        self.full = []
        self.code_len_count = []
        self.code_set = {}
        self.remain1 = {}
        self.remain2 = {}
        self.remain3 = {}
        self.retry = []
        self.sp = []
        self.ci = set()
        self.count = 0
        self.content = []
        for i in range(0, 30):
            self.code_len_count.append(0)


def round1(dataContext):
     for line in dataContext.content:
        dataContext.count += 1
        line = line.strip().decode(dataContext.coding)
        if line == "" or line[0] == '#':
            continue
        try:
            kv = line.split(None)
            w = kv[0]
            c = kv[1]
            cc = c.split(':')
            key_single = cc[0][0]
            key_front = cc[0]
            key_ext = cc[1]
            dataContext.ci.add(w + u'\t' + key_front)
            cc_full = key_front + key_ext
            if key_single not in dataContext.code_set:
                dataContext.code_set[key_single] = 1
                code_len = len(key_single)
                dataContext.code_len_count[code_len] += 1
                dataContext.result.append(w + "\t" + key_single + "\t1")
                dataContext.full.append(w + "\t" + cc_full)
            elif key_front not in dataContext.code_set:
                dataContext.code_set[key_front] = 1
                code_len = len(key_front)
                dataContext.code_len_count[code_len] += 1
                dataContext.result.append(w + "\t" + key_front + "\t2")
                dataContext.full.append(w + "\t" + cc_full)
            else:
                kl = len(key_front)
                if kl == 1:
                    remain = dataContext.remain1
                elif kl == 2:
                    remain = dataContext.remain2
                elif kl == 3:
                    remain = dataContext.remain3
                else:
                    print "The key is too long. ", key_front, kl

                item = (w, key_front, key_ext)
                if cc_full in remain:
                    remain[cc_full].append(item)
                else:
                    remain[cc_full] = [item]
        except Exception as err:
            logging.error(err)


def round2(dataContext):
    round2process(dataContext, dataContext.remain1)
    round2process(dataContext, dataContext.remain2)
    round2process(dataContext, dataContext.remain3)

def round2process(dataContext, remain):
    for (k, vs) in remain.iteritems():
        if len(vs) == 1:
            # 为了减少重码，把简码的机会留给可能有重码的字。
            # 这里先可能会导致顺序在前面的反而是全码。
            #dataContext.retry.append((k, vs[0]))
            v = vs[0]
            code_ext = v[1] + v[2][0]
            if code_ext not in dataContext.code_set:
                dataContext.code_set[code_ext] = 1
                code_len = len(code_ext)
                dataContext.code_len_count[code_len] += 1
                dataContext.result.append(v[0] + "\t" + code_ext + "\t3")
                dataContext.full.append(v[0] + "\t" + k)
            elif k not in dataContext.code_set:
                dataContext.code_set[k] = 1
                code_len = len(k)
                dataContext.code_len_count[code_len] += 1
                dataContext.result.append(v[0] + "\t" + k + "\t4")
            else:
                dataContext.code_set[k] += 1
                code_len = len(k)
                dataContext.code_len_count[code_len] += 1
                dataContext.result.append(v[0] + "\t" + k + "\t5")
            continue
        for v in vs:
            code_ext = v[1] + v[2][0]
            if code_ext not in dataContext.code_set:
                dataContext.code_set[code_ext] = 1
                code_len = len(code_ext)
                dataContext.code_len_count[code_len] += 1
                dataContext.result.append(v[0] + "\t" + code_ext + "\t3")
                dataContext.full.append(v[0] + "\t" + k)
            elif k not in dataContext.code_set:
                dataContext.code_set[k] = 1
                code_len = len(k)
                dataContext.code_len_count[code_len] += 1
                dataContext.result.append(v[0] + "\t" + k + "\t4")
            else:
                dataContext.code_set[k] += 1
                code_len = len(k)
                dataContext.code_len_count[code_len] += 1
                dataContext.result.append(v[0] + "\t" + k + "\t5")

def round3(dataContext):
    retry = dataContext.retry

    for x in retry:
        # print x
        k = x[0]
        v = x[1]

        code_ext = v[1] + v[2][0]
        if code_ext not in dataContext.code_set:
            dataContext.code_set[code_ext] = 1
            code_len = len(code_ext)
            dataContext.code_len_count[code_len] += 1
            dataContext.result.append(v[0] + "\t" + code_ext + "\t3")
            dataContext.full.append(v[0] + "\t" + k)
        elif k not in dataContext.code_set:
            dataContext.code_set[k] = 1
            code_len = len(k)
            dataContext.code_len_count[code_len] += 1
            dataContext.result.append(v[0] + "\t" + k + "\t4")
        else:
            dataContext.code_set[k] += 1
            code_len = len(k)
            dataContext.code_len_count[code_len] += 1
            dataContext.result.append(v[0] + "\t" + k + "\t5")


def make_short(infile, coding='utf-8'):
    (short_name, extension) = os.path.splitext(infile)
    out_file = '%s-out%s' % (short_name, extension)
    out_full_file = '%s-full%s' % (short_name, extension)
    out_ci_file = '%s-ci%s' % (short_name, extension)

    output_file = open(out_file, "w")
    output_full_file = open(out_full_file, "w")
    output_ci_file = open(out_ci_file, "w")

    print "%s: begin to process %s." % (sys.argv[0], infile)
    dataContext = DataContext()
    dataContext.coding = coding
    with open(infile) as f:
        dataContext.content = f.read().splitlines()

    # round 1
    round1(dataContext)

    print '%s: processed %s words' % (sys.argv[0], dataContext.count)
    print ("%s: %d words. And %d codes." % (sys.argv[0], len(dataContext.result), len(dataContext.code_set)))

    print("%s: process remain..." % sys.argv[0])
    # round 2
    round2(dataContext)
    print ("%s: %d words. And %d codes." % (sys.argv[0], len(dataContext.result), len(dataContext.code_set)))

    # round 3
    round3(dataContext)
    print ("%s: %d words. And %d codes." % (sys.argv[0], len(dataContext.result), len(dataContext.code_set)))


    for line in dataContext.full:
         print >> output_full_file, line.encode(coding)
    for line in dataContext.result:
        print >> output_file, line.encode(coding)
    output_file.close()

#    for line in full:
#        print >> output_full_file, line.encode(coding)
    output_full_file.close()

    for line in dataContext.ci:
        print >> output_ci_file, line.encode(coding)
    output_ci_file.close()

    print "%s: Output to %s and %s" % (sys.argv[0], out_file, out_full_file)
    print "%s: All %d words. And %d codes." % (sys.argv[0], len(dataContext.result), len(dataContext.code_set))
    print "%s: Code Length Count: %s" % (sys.argv[0], dataContext.code_len_count)


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
