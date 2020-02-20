# -*- coding: utf-8-*- 

print "load accept phrases..."
f = open(u"data\胡音卷舌.txt")
zhiu_set = set()
for line in f:
	line = line.strip().decode("utf-8")
	zhiu_set.add(line.split()[0])
f.close()

def accept (w):
	if w in zhiu_set:
		return True
	else:
		return False


f = open(u"data\\cantonese-common.single.sort.txt")
f1 = open(u"out\\new.txt", "w")
count=0
phrases = set()
for line in f:
	count += 1
	line = line.strip().decode("utf-8")
	if line == "" or line[0] == "#":
		continue
	kv = line.split()
	phrases.add(kv[0])
	if accept(kv[0]) and kv[1][0] in 'zcs':
		print >> f1, ("%s\t%s" % (kv[0], kv[1][0] + 'h' + kv[1][1:])).encode("utf-8")
	else:
		print >> f1, line.encode("utf-8")
	if count % 1000 == 0:
		print "processed %d lines." % count
print "processed %d lines." % count
f.close()
f1.close()

	