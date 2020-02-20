#-*- coding:utf-8-*-
#  create empty code set

f = open("data/syllable.txt");
syllables = set()
for line in f:
	line = line.strip();
	if line == "" or line[0] == "#":
		continue
	syllables.add(line)
f.close()

f = open("data/syllable-empty.txt", "w");
li = [chr(i) for i in range(ord("a"),ord("z")+1)]
for a1 in li:
	for a2 in li:
		code = a1+a2
		if code not in syllables:
			print >> f, code
f.close()