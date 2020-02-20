#!/usr/bin/env python3
# coding: utf-8

f1 = open(u'all.txt', encoding='utf-8') #utf-16le
f2 = open(u'拼音.txt', encoding='utf-8')
f3 = open(u'7拼音反查.txt', 'w', encoding='utf-16le')

#f2 = open(u'笔画.txt', encoding='utf-8')
#f3 = open(u'8笔画反查.txt', 'w', encoding='utf-16le')


# load keys
keys = {}

for line in f1:
    line = line.strip()
    if (line != '' and line[0] == '\ufeff'):
        line = line[1:]
    if line == '' or line[0] == '#' or line[0] == '-':
        continue
    kv = line.split(None)
    if kv[0] not in keys:
        keys[kv[0]] = kv[1]
f1.close()
print('keys loaded')

f3.write('\ufeff')

for line in f2:
    line = line.strip()
    if (line != "" and line[0] == '\ufeff'):
        line = line[1:]
    if line == '' or line[0] == '#' or line[0] == '-':
        f3.write(line)
        f3.write('\n')
        continue
    
    kv = line.split()
    if kv[0] in keys:
        f3.write('$ddcmd(%s,%s[%s])\t%s\n'
                 % (kv[0], kv[0], keys[kv[0]], kv[1]))
    else:
        f3.write('%s\n' % line)
f2.close()
f3.close()

print('DONE!')