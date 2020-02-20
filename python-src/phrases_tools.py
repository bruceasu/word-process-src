#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os


def clean_dup_phrases(in_file="../out/phrases-out.txt",
                      class_1=10000,
                      class_1_cnt=3,
                      class_2=30000,
                      class_2_cnt=3,
                      other_cnt=1,
                      encoding='utf8',
                      filterFn=None
                      ):
    """ clean for leave the frequency phrases.
    
    :param in_file: phrases file, format - work codes
    :param class_1: first class phrases
    :param class_1_cnt: leaving count of phrases
    :param class_2: second class phrases
    :param class_2_cnt: leaving count of phrases
    :param other_cnt: the other class leaving count of phrases
    :param encoding: file encoding, default is utf8
    :return: None.
    """
    (short_name, extension) = os.path.splitext(in_file)
    out_file = '%s-out%s' % (short_name, extension)
    err_file = '%s-err%s' % (short_name, extension)

    input_file = open(in_file, "r")
    output_file = open(out_file, "w")
    err_file = open(err_file, "w")

    print "Begin to process ", in_file

    code_set = {}
    cnt = 0
    out_cnt = 0
    not_accept_cnt = 0
    for line in input_file:
        line = line.strip().decode(encoding)
        if line == "" or line[0] == '#':
            continue
        try:
            cnt += 1
            kv = line.split(None)
            w = kv[0]
            c = kv[1]
            if filterFn:
                if c in code_set:
                    d_count = code_set[c]
                else:
                    d_count = 0
                if not filterFn(w, c, cnt, d_count):
                    # print 'not accept: %s %s' % (w, c)
                    not_accept_cnt += 1
                    continue

            if c not in code_set:
                code_set[c] = 1
                print >> output_file, line.encode(encoding)
                out_cnt += 1
            else:
                if cnt < class_1:
                    if code_set[c] < class_1_cnt:
                        code_set[c] += 1
                        print >> output_file, line.encode(encoding)
                        out_cnt += 1
                    else:
                        print >> err_file, ("ignore %s" % line).encode(encoding)
                elif cnt < class_2:
                    if code_set[c] < class_2_cnt:
                        code_set[c] += 1
                        print >> output_file, line.encode(encoding)
                        out_cnt += 1
                    else:
                        print >> err_file, ("ignore %s" % line).encode(encoding)
                else:
                    if code_set[c] < other_cnt:
                        code_set[c] += 1
                        print >> output_file, line.encode(encoding)
                        out_cnt += 1
                    else:
                        print >> err_file, ("ignore %s" % line).encode(encoding)

        except Exception as err:
            print err
            print line
    input_file.close()

    print "DONE! Output to %s and %s" % (out_file, err_file)
    print "All %d phrases, and accept %d phrases." % (cnt, out_cnt)
    print "Codes: ", len(code_set)
    print "Not accept count: ", not_accept_cnt

    # 30
    cnt_of_codes = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    for k, v in code_set.iteritems():
            cnt_of_codes[v] += 1
    print "Count of codes: ", cnt_of_codes


def remove_tab(phrases_file, out_file=None, charset='utf-8'):
    if out_file is None or out_file == '':
        (short_name, extension) = os.path.splitext(phrases_file)
        out_file = '%s.out%s' % (short_name, extension)
    print "remove_tab  %s." % phrases_file
    iFile = open(phrases_file)
    oFile = open(out_file, 'w')
    count = 0
    for line in iFile:
        count += 1
        line = line.decode(charset).strip()
        if line == '' or line[0] == '#':
            continue
        kv = line.split('\t')
        word = kv[0]
        codes = kv[2].replace(' ', '')
        print >> oFile, ("%s\t%s" % (word, codes)).encode(charset)
        if count % 1000 == 0:
            print 'processed %d words' %  count
    print 'processed %d words' %  count
    print " %s written!" % out_file
    return out_file

if __name__ == '__main__':
    clean_dup_phrases()
