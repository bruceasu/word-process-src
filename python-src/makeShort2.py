#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import logging

logging.basicConfig(level=logging.DEBUG,
                format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s %(message)s',
                datefmt='%a, %d %b %Y %H:%M:%S',
                filename='..\\out\\myapp.log',
                filemode='w')

# inputFile = open("t.txt", encoding='utf_8_sig')

infile = input("input [t.txt]: ")
if infile == "":
    infile = "t.txt"

coding = input("file encoding [utf-8]: ")
if coding == '':
    coding = 'utf-8'

out_file = infile + '.out.txt'
out_full_file = infile + '.full.txt'
out_ci_file = infile + '.ci.txt'

inputFile = open(infile, encoding=coding)
output1 = open(out_file, "w", encoding=coding)
output2 = open(out_full_file, "w", encoding=coding)
output3 = open(out_ci_file, "w", encoding=coding)

print ("Begin to process ", infile)

code_set = {}
result = []
full = []
freq = 1000000
code_len_count = []
for i in range(0, 30):
    code_len_count.append(0)

remain = []
ci = set()

lines = []
for line in inputFile:
    line = line.strip()
    if line == "" or line[0] == '#':
        continue
    try:
        kv = line.split(None)
        w = kv[0]
        c = kv[1]
        cc = c.split(':')
        length = len(cc[0])
        if length != 1:
            lines.append(line)
            continue
        else:
            if not cc[0] in code_set:
                code_set[cc[0]] = 1
                code_len = len(cc[0])
                code_len_count[code_len] += 1
                result.append(w + "\t" + cc[0] + "\n")
            else:
                lines.append(line)
                continue
    except Exception as err:
        print(err)
        print(line)
inputFile.close()

for line in lines:
    line = line.strip()
    if line == "" or line[0] == '#':
        continue
    try:
        kv = line.split(None)
        w = kv[0]
        c = kv[1]
        cc = c.split(':')
        key_single = cc[0][0]
        key_yamjit = cc[0]

        if key_single not in code_set:
            code_set[key_single] = 1
            code_len = len(key_single)
            code_len_count[code_len] += 1
            result.append(w + "\t" + key_single + "\n")
        elif key_yamjit not in code_set:
            code_set[key_yamjit] = 1
            code_len = len(key_yamjit)
            code_len_count[code_len] += 1
            result.append(w + "\t" + key_yamjit + "\n")
        else:
            remain.append(line)
    except Exception as err:
        logging.error(err)
        logging.error(line.encode('GBK'))  # maybe 1 code

for line in remain:
    try:
        kv = line.split(None)
        w = kv[0]
        c = kv[1]
        cc = c.split(':')
        added = False
        for padding in range(0, len(cc[1])):
            key_with_ext = cc[0] + cc[1][0:padding]
            logging.debug("%s %s" % (w, key_with_ext))
            if key_with_ext not in code_set:
                code_set[key_with_ext] = 1
                code_len = len(key_with_ext)
                code_len_count[code_len] += 1
                result.append(w + "\t" + key_with_ext + "\n")
                added = True
                break

        if not added:
            logging.debug ("add all code:%s" % line.encode('GBK'))
            code_set[cc[0] + cc[1]] = 1
            code_len_count[len(cc[0] + cc[1])] += 1
            result.append(w + "\t" + cc[0] + cc[1] + "\n")
    except Exception as err:
        logging.error(err)
        logging.error(line.encode('GBK'))

print ("result: %d" % len(result))

output1.writelines(result)
output1.close()

output2.writelines(full)
output2.close()

output3.writelines(ci)
output3.close()

print ("DONE! Output to %s and %s" % (out_file, out_full_file))
print ("All %d words. And %d codes." % (len(result), len(code_set)))
print ("code length count: ", code_len_count)

