#-*- coding: utf-8 -*
import sys
import os


print "%s: Load common words." % sys.argv[0]
common_file = open('common-words.txt')
common_set = set()
for line in common_file:
	line = line.strip().decode("utf-8")
	if line == "" or line[0] == '#':
		continue
	common_set.add(line)
common_file.close()

in_file = open("gxcx.txt", "r")
out_file1 = open("gxcx.single-common.txt", "w")
out_file2 = open("gxcx.single-not-common.txt", "w")
out_file3 = open("gxcx.phrases.txt", "w")
coding="utf-8"
for line in in_file:
	line = line.strip().decode(coding)
	if line == "" or line[0] == '#':
		continue
	(w, c) = line.split()
	if len(w) > 1:
		print >> out_file3, line.encode(coding)
	elif w in common_set:
		print >> out_file1, line.encode(coding)
	else:
		print >> out_file2, line.encode(coding)

in_file.close();
out_file1.close();
out_file2.close();
out_file3.close();

print "DONE"