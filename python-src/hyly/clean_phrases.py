# -*- coding: utf-8-*- 

print "load accept phrases..."
f = open(u"data/奇境词库2.0纯净版-ext.TXT")
accept_set = set()
for line in f:
	line = line.strip().decode("utf-8")
	accept_set.add(line)
f.close()
def accept (w):
	if w in accept_set:
		return True
	else:
		return False


f = open("data/he_phrases.txt")
f1 = open("data/accept.txt", "w")
f2 = open("data/not_accept.txt", "w")
f3 = open(u"data/not_in.txt", "w")
count=0
phrases = set()
for line in f:
	count += 1
	line = line.strip().decode("utf-8")
	if line == "" or line[0] == "#":
		continue
	kv = line.split()
	phrases.add(kv[0])
	if accept(kv[0]):
		print >> f1, line.encode("utf-8")
	else:
		print >> f2, line.encode("utf-8")
	if count % 1000 == 0:
		print "processed %d lines." % count
print "processed %d lines." % count
for w in accept_set:
	if w not in phrases:
		print >> f3, w.encode("utf-8")
f.close()
f1.close()
f2.close()
f3.close()

	