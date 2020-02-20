#!/usr/bin/env python
# encoding: utf-8

__author__ = 'Bruce Suk'

import sys
import os


class PhraseMaker:
    def __init__(self, dic_file='../../out/merged.txt', encoding='utf-8'):
        self.dic_file = dic_file
        self.encoding = encoding
        self.dict_map = {}
        self.load_dict()

    def load_dict(self):
        f = open(self.dic_file)
        print'load ', self.dic_file, ' ...'
        for line in f:
            line = line.strip().decode(self.encoding)
            if line == '' or line[0] == '#':
                continue
            kv = line.split(None)
            # print kv
            if kv[0] in self.dict_map:
                if len(kv[1]) > len(self.dict_map[kv[0]]):
                    self.dict_map[kv[0]] = kv[1]
            else:
                self.dict_map[kv[0]] = kv[1]
        f.close()
        return self.dict_map

    def load(self, string):
        codes = []
        for x in string:
            if x not in self.dict_map:
                raise Exception('no this word. %s in %s' % (x, string))
        for x in string:
            codes.append([self.dict_map[x]])
        return codes

    def make_phrases(
            self, phrase_file='../../../words-and-phrases-data/phrase-data/phrases.min.txt', out_file='../../out/phrases.txt', encoding='utf-8'):
        (short_name, extension) = os.path.splitext(out_file)
        err_file = '%s-err%s' % (short_name, extension)
        f2 = open(phrase_file)
        f3 = open(out_file, 'w')
        f4 = open(err_file, 'w')

        print 'phrases file: %s\nout file: %s\nerror file: %s\n' % (phrase_file, out_file, err_file)
        count = 0
        for line in f2:
            try:
                line = line.strip().decode(encoding)
                if line == '' or line[0] == '#':
                    continue
                count += 1
                length = len(line)
                # print (line, length)
                codes = self.load(line)
                if length == 1:
                    pass
                elif length == 2:
                    for c1 in codes[0]:
                        for c2 in codes[1]:
                            first = c1[0]
                            kv = c2.split(':')
                            last = kv[0][0] + kv[1]
                            print >> f3, (u"%s\t%s%s" % (line, first, last)).encode(encoding)
                elif length == 3:
                    for c1 in codes[0]:
                        for c2 in codes[1]:
                            for c3 in codes[2]:
                                first = c1[0]
                                second = c2[0]
                                kv = c3.split(':')
                                third = kv[0][0] + kv[1][0]
                                print >> f3, (u"%s\t%s%s%s" % (line, first, second, third)).encode(encoding)
                else:
                    for c1 in codes[0]:
                        for c2 in codes[1]:
                            for c3 in codes[2]:
                                for c4 in codes[-1]:
                                    first = c1[0]
                                    second = c2[0]
                                    third = c3[0]
                                    kv = c4.split(':')
                                    fourth = kv[0][0] + kv[1][0]
                                    print >> f3, (u"%s\t%s%s%s%s"
                                                  % (line, first, second, third, fourth)).encode(encoding)
                if count % 1000 == 0:
                    print 'processed phrases:', count
            except Exception as err:
                print >> f4, line.encode(encoding)
        f2.close()
        f3.close()
        f4.close()
        print("done!")

    @staticmethod
    def cleanup_phrases(phrases, times=-1, encoding='utf-8'):
        (short_name, extension) = os.path.splitext(phrases)
        out_file = '%s.out%s' % (short_name, extension)
        print "%s: cleanup  %s." % (sys.argv[0], phrases)
        ifile = open(phrases)
        ofile = open(out_file, 'w')
        count = 0
        ps = {}
        for line in ifile:
            count += 1
            line = line.strip().decode(encoding)
            if line == '' or line[0] == '#':
                continue
            kv = line.split('\t')
            phrase = kv[0]
            code = kv[1]
            if code in ps:
                ps[code].append(phrase)
            else:
                ps[code] = [phrase]
            if count % 1000 == 0:
                print '%s: processed %d words' % (sys.argv[0], count)
        print '%s: processed %d words' % (sys.argv[0], count)
        for k, v in ps.iteritems():
            if times == -1:
                for p in v:
                    print >> ofile, ("%s\t%s" % (p, k)).encode(encoding)
            else:
                for p in v[0:min(len(v),times)]:
                    print >> ofile, ("%s\t%s" % (p, k)).encode(encoding)
        print "%s: %s written!" % (sys.argv[0], out_file)

if __name__ == '__main__':
    pm = PhraseMaker()
    pm.make_phrases()
    PhraseMaker.cleanup_phrases('../../out/phrases.txt', 5)
