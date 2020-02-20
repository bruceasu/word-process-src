#!/usr/bin/env python3
# -*- codingL utf-8 -*-

import os, sys

f1 = open('..\\..\\words-and-phrases-data\\common-data\\common-hz.txt', encoding='utf-8')
f2 = open('common_hz.py', 'w', encoding='utf-8')

f2.write('#!/usr/bin/env python\n')
f2.write('# -*- coding: utf-8 -*-\n')
f2.write('common_words_list = [\n        ')
count = 0
for line in f1:
    line = line.strip()
    if line == '' or line.startswith('#'):
        continue
    count +=1
    f2.write("u'%s'," % line)
    if count % 16 == 0:
        f2.write('\n        ')
    if count % 1000 == 0:
        print("processed %d lines." % count)
f2.write('\n    ]\n')
f2.write('common_words_set = set(common_words_list)')
print("processed %d lines." % count)
print('DONE! written to common_hz.py')
