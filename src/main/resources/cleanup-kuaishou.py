# coding: utf-8
base_file= u'common-words-7600.txt'
in_file = u'kuaishou.txt'
out_file = u'kuaishou.less.txt'
out_file2 = u'kuaishou.remove.txt'

base = open(base_file)
base_set = set()
for w in base:
	w = w.strip().decode("utf-8")
	if w == "" or w[0] == "#":
		continue
	base_set.add(w);
base.close()

input = open(in_file)
output = open(out_file, "w")
output2 = open(out_file2, "w")
for line in input:
	line = line.strip().decode("utf-8")
	if line == "" or line[0] == "#":
		continue
	kv = line.split()
	w = kv[0]
	if w in base_set:
		print >> output, line.encode("utf-8")
	else:
		print >> output2, line.encode("utf-8")
output.close()
output2.close()
input.close()
print "DONE!"
