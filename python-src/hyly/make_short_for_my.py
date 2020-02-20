#!/usr/bin/env python
# -*- coding: utf-8 -*-

import logging
import sys
import os

def load_common_words():
        print "%s: Load common words." % sys.argv[0]
        common_file = open('data/500.txt')
        common_set = set()
        for line in common_file:
                line = line.strip().decode("utf-8")
                if line == "" or line[0] == '#':
                        continue
                common_set.add(line)
        common_file.close()
        print "common words loaded."
        return common_set

def load_common_words2():
        print "%s: Load common words." % sys.argv[0]
        common_file = open('data/common-words-1600.txt')
        common_set = set()
        for line in common_file:
                line = line.strip().decode("utf-8")
                if line == "" or line[0] == '#':
                        continue
                common_set.add(line)
        common_file.close()
        print "common words 2 loaded."
        return common_set

def load_common_words3():
        print "%s: Load common words." % sys.argv[0]
        common_file = open('data/gb2312-1.txt')
        common_set = set()
        for line in common_file:
                line = line.strip().decode("utf-8")
                if line == "" or line[0] == '#':
                        continue
                common_set.add(line)
        common_file.close()
        print "common words 3 loaded."
        return common_set

def load_common_words4():
        print "%s: Load common words." % sys.argv[0]
        common_file = open('data/common-words.txt')
        common_set = set()
        for line in common_file:
                line = line.strip().decode("utf-8")
                if line == "" or line[0] == '#':
                        continue
                common_set.add(line)
        common_file.close()
        print "common words 4 loaded."
        return common_set
        
def load_one_set():
        """ 加载固定简码表
        """
        print "loading one_set ..."
        one_set = {}
        #f = open("data/1_2.txt")
        f = open("data/he_1_2.txt")
        for line in f:
                line = line.strip().decode("utf-8")
                if line == u"" or line[0] == u"#":
                        continue
                word, code = line.split()
                one_set[word] = code
        print "one_set loaded."
        return one_set

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
        out_other_file = '%s-other%s' % (short_name, extension)
        input_file = open(infile)
        output_file = open(out_file, "w")
        output_full_file = open(out_full_file, "w")
        output_ci_file = open(out_ci_file, "w")
        output_other_file = open(out_other_file, "w")
        common_set  = load_common_words()
        common_set2 = load_common_words2()
        common_set3 = load_common_words3()
        common_set4 = load_common_words4()
        one_set = {} # load_one_set()

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
        padding = ['', ';', ',', '.', '/']
        for k, v in one_set.iteritems():
                if v in code_set:
                        code_set[v] += 1
                else:
                        code_set[v] = 1
                code_len_count[len(v)] += 1
                if len(v) == 1:
                        result.append(k + "\t" + v + "\t1")
                else:
                        result.append(k + "\t" +  v + "\t1")
        set_500 = []
        set_1600 = []
        set_5000 = []
        set_3700 = []
        set_other = {}
        for line in input_file:
                count += 1
                line = line.strip().decode(coding)
                if line == "" or line[0] == '#':
                        continue
                kv = line.split(None)
                w = kv[0]
                c = kv[1]
                cc = c.split(':')
                key_single = cc[0][0]
                key_syllable = cc[0:2]
                key_syllable2 = cc[1]
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
                    full.append(w + "\t" + cc_full)
                elif w in common_set:
                        set_500.append((w, cc[0], cc[1]))
                elif w in common_set2:
                        set_1600.append((w, cc[0], cc[1]))
                elif w in common_set3:
                        set_3700.append((w, cc[0], cc[1]))
                elif w in common_set4:
                        set_5000.append((w, cc[0], cc[1]))
                else:
                        if cc_full not in set_other:
                            set_other[cc_full] = [w];
                        else:
                            set_other[cc_full].append(w);

        for (w, k1, k2) in set_500:
                key_syllable = k1[0:2]
                key_syllable2 = k1
                key_ext = k1 + k2[0]
                cc_full = k1 + k2
                if key_syllable not in code_set:
                        code_set[key_syllable] = 1
                        code_len = len(key_syllable)
                        code_len_count[code_len] += 1
                        result.append(w + "\t" + key_syllable + "\t2")
                        full.append(w + "\t" + cc_full)
                elif key_syllable in code_set and code_set[key_syllable] < 1:
                        new_code = key_syllable + padding[code_set[key_syllable]]
                        code_set[key_syllable] += 1
                        code_set[new_code] = 1
                        code_len = len(new_code)
                        code_len_count[code_len] += 1
                        result.append(w + "\t" + new_code + "\t2")
                        full.append(w + "\t" + cc_full)
                elif not key_syllable2 in code_set:
                        code_set[key_syllable2] = 1
                        code_len = len(key_syllable2)
                        code_len_count[code_len] += 1
                        result.append(w + "\t" + key_syllable2 + "\t2")
                        full.append(w + "\t" + cc_full)
                elif key_ext not in code_set:
                        code_set[key_ext] = 1
                        code_len = len(key_ext)
                        code_len_count[code_len] += 1
                        result.append(w + "\t" + key_ext + "\t2")
                        full.append(w + "\t" + cc_full)
                elif key_ext in code_set and code_set[key_ext] < 1:
                        new_code = key_ext + padding[code_set[key_ext]]
                        code_set[new_code] = 1
                        code_set[key_ext] += 1
                        code_len = len(key_ext)
                        code_len_count[code_len] += 1
                        result.append(w + "\t" + new_code + "\t2")
                        full.append(w + "\t" + cc_full)
                #elif cc_full not in code_set:
                #        code_set[cc_full] = 1
                #        code_len = len(cc_full)
                #        code_len_count[code_len] += 1
                #        result.append(w + "\t" + cc_full + "\t3")
                else:
                        if cc_full not in set_other:
                            set_other[cc_full] = [w];
                        else:
                            set_other[cc_full].append(w);

        for (w, k1, k2) in set_1600:
                key_syllable = k1[0:2]
                key_syllable2 = k1
                key_ext = k1 + k2[0]
                cc_full = k1 + k2
                if key_syllable not in code_set:
                        code_set[key_syllable] = 1
                        code_len = len(key_syllable)
                        code_len_count[code_len] += 1
                        result.append(w + "\t" + key_syllable + "\t3")
                        full.append(w + "\t" + cc_full)
                elif not key_syllable2 in code_set:
                        code_set[key_syllable2] = 1
                        code_len = len(key_syllable2)
                        code_len_count[code_len] += 1
                        result.append(w + "\t" + key_syllable2 + "\t3")
                        full.append(w + "\t" + cc_full)
                elif key_ext not in code_set:
                        code_set[key_ext] = 1
                        code_len = len(key_ext)
                        code_len_count[code_len] += 1
                        result.append(w + "\t" + key_ext + "\t3")
                        full.append(w + "\t" + cc_full)

                #elif cc_full not in code_set:
                #        code_set[cc_full] = 1
                #        code_len = len(cc_full)
                #        code_len_count[code_len] += 1
                #        result.append(w + "\t" + cc_full + "\t3")
                else:
                        if cc_full not in set_other:
                            set_other[cc_full] = [w];
                        else:
                            set_other[cc_full].append(w);
        for (w, k1, k2) in set_3700:
                key_syllable = k1[0:2]
                key_syllable2 = k1
                key_ext = k1 + k2[0]
                cc_full = k1 + k2
                if key_syllable not in code_set:
                        code_set[key_syllable] = 1
                        code_len = len(key_syllable)
                        code_len_count[code_len] += 1
                        result.append(w + "\t" + key_syllable + "\t4")
                        full.append(w + "\t" + cc_full)
                elif not key_syllable2 in code_set:
                        code_set[key_syllable2] = 1
                        code_len = len(key_syllable2)
                        code_len_count[code_len] += 1
                        result.append(w + "\t" + key_syllable2 + "\t4")
                        full.append(w + "\t" + cc_full)
                elif key_ext not in code_set:
                        code_set[key_ext] = 1
                        code_len = len(key_ext)
                        code_len_count[code_len] += 1
                        result.append(w + "\t" + key_ext + "\t4")
                        full.append(w + "\t" + cc_full)
                #elif cc_full not in code_set:
                #        code_set[cc_full] = 1
                #        code_len = len(cc_full)
                #        code_len_count[code_len] += 1
                #        result.append(w + "\t" + cc_full + "\t4")
                else:
                        if cc_full not in set_other:
                            set_other[cc_full] = [w];
                        else:
                            set_other[cc_full].append(w);
        for (w, k1, k2) in set_5000:
                key_syllable = k1[0:2]
                key_syllable2 = k1
                key_ext = k1 + k2[0]
                cc_full = k1 + k2
                if key_syllable not in code_set:
                        code_set[key_syllable] = 1
                        code_len = len(key_syllable)
                        code_len_count[code_len] += 1
                        result.append(w + "\t" + key_syllable + "\t5")
                        full.append(w + "\t" + cc_full)
                elif not key_syllable2 in code_set:
                        code_set[key_syllable2] = 1
                        code_len = len(key_syllable2)
                        code_len_count[code_len] += 1
                        result.append(w + "\t" + key_syllable2 + "\t5")
                        full.append(w + "\t" + cc_full)
                elif key_ext not in code_set:
                        code_set[key_ext] = 1
                        code_len = len(key_ext)
                        code_len_count[code_len] += 1
                        result.append(w + "\t" + key_ext + "\t5")
                        full.append(w + "\t" + cc_full)
                #elif cc_full not in code_set:
                #        code_set[cc_full] = 1
                #        code_len = len(cc_full)
                #        code_len_count[code_len] += 1
                #        result.append(w + "\t" + cc_full + "\t5")
                else:
                        if cc_full not in set_other:
                            set_other[cc_full] = [w];
                        else:
                            set_other[cc_full].append(w);

        print '%s: processed %s words' % (sys.argv[0], count)
        input_file.close()
        print ("%s: %d words. And %d codes." % (sys.argv[0], (len(result) +len(sp)), len(code_set)))

        for line in full:
                kv = line.split()
                w = kv[0]
                cc_full = kv[1]
                if cc_full not in set_other:
                        set_other[cc_full] = [w];
                else:
                        set_other[cc_full].append(w);
                #if kv[1] not in code_set:
                #        code_set[kv[1]] = 1
                #        result.append(line + "\t20")
                #else:
                #        code_set[kv[1]] += 1
                #        result.append(line + "\t30")
                #code_len = len(kv[1])
                #code_len_count[code_len] += 1
        for line in sp:
                kv = line.split()
                w = kv[0]
                key_ext = kv[1][0:3]
                cc_full = kv[1]
            #if cc_full not in code_set:
            #    code_set[cc_full] = 1
            #    code_len = len(cc_full)
            #    code_len_count[code_len] += 1
            #    print >> output_full_file, (w + "\t" + key_ext).encode(coding)
            #print >> output_full_file, line.encode(coding)
                if cc_full not in set_other:
                        set_other[cc_full] = [w];
                else:
                        set_other[cc_full].append(w);
        for (k, ws) in set_other.iteritems():
                length = len(ws)
                key_ext = k[0:3]
                if length == 1:
                        if key_ext not in code_set:
                            code_set[key_ext] = 1
                            code_len = len(key_ext)
                            code_len_count[code_len] += 1
                            result.append(w + "\t" + key_ext + "\t5")
                            print >> output_full_file, ("%s\t%s" % (ws[0], k)).encode(coding)
                        elif ws[0] in one_set:
                                result.append("%s\t%s\t6" % (ws[0], k))
                        elif (ws[0], k[0:3], k[3:]) in set_500:
                                result.append("%s\t%s\t6" % (ws[0], k))
                        elif (ws[0], k[0:3], k[3:]) in set_1600:
                                result.append("%s\t%s\t7" % (ws[0], k))
                        elif (ws[0], k[0:3], k[3:]) in set_3700:
                                result.append("%s\t%s\t8" % (ws[0], k))
                        elif (ws[0], k[0:3], k[3:]) in set_5000:
                                result.append("%s\t%s\t9" % (ws[0], k))
                        else:
                                print >> output_full_file, ("%s\t%s" % (ws[0], k)).encode(coding)
                        code_set[k] = 1
                        code_len = len(k)
                        code_len_count[code_len] += 1
                else:
                        if key_ext not in code_set:
                            code_set[key_ext] = 1
                            code_len = len(key_ext)
                            code_len_count[code_len] += 1
                            result.append(w + "\t" + key_ext + "\t5")
                            print >> output_full_file, ("%s\t%s" % (ws[0], k)).encode(coding)
                        elif ws[0] in one_set:
                                result.append("%s\t%s\t16" % (ws[0], k))
                        elif (ws[0], k[0:3], k[3:]) in set_500:
                                result.append("%s\t%s\t16" % (ws[0], k))
                        elif (ws[0], k[0:3], k[3:]) in set_1600:
                                result.append("%s\t%s\t17" % (ws[0], k))
                        elif (ws[0], k[0:3], k[3:]) in set_3700:
                                result.append("%s\t%s\t18" % (ws[0], k))
                        elif (ws[0], k[0:3], k[3:]) in set_5000:
                                result.append("%s\t%s\t19" % (ws[0], k))
                        else:
                                print >> output_full_file, ("%s\t%s" % (ws[0], k)).encode(coding)
                        code_set[k] = 1
                        code_len = len(k)
                        code_len_count[code_len] += 1
                        padding = "0;adfghjkl"
                        # 全码重码
                        for i in xrange(1, length):
                                new_code = k # + padding[i]
                                if ws[0] in one_set:
                                        result.append("%s\t%s\t%s" % (ws[0], new_code, (i + 20)))
                                elif (ws[0], k[0:3], k[3:]) in set_500:
                                        result.append("%s\t%s\t%s" % (ws[i], new_code, (i + 20)))
                                elif (ws[0], k[0:3], k[3:]) in set_1600:
                                        result.append("%s\t%s\t%s" % (ws[i], new_code, (i + 20)))
                                elif (ws[0], k[0:3], k[3:]) in set_3700:
                                        result.append("%s\t%s\t%s" % (ws[i], new_code, (i + 20)))
                                elif (ws[0], k[0:3], k[3:]) in set_5000:
                                        result.append("%s\t%s\t%s" % (ws[i], new_code, (i + 20)))
                                else:
                                        print >> output_full_file, ("%s\t%s" % (ws[i], new_code)).encode(coding)
                                code_set[new_code] = 1
                                code_len = len(new_code)
                                code_len_count[code_len] += 1
        output_full_file.close()

        for line in result:
                print >> output_file, line.encode(coding)
        output_file.close()

        for line in ci:
                print >> output_ci_file, line.encode(coding)
        output_ci_file.close()

        output_other_file.close()
        print "%s: Output to %s and %s" % (sys.argv[0], out_file, out_full_file)
        print "%s: All %d codes." % (sys.argv[0], len(code_set))
        print "%s: Code Length Count: %s" % (sys.argv[0], code_len_count)


def main():
        infile = 'out/hyly.single.merged.txt'
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
