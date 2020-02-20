#!/usr/bin/env python
import sys
def split(in_file, out_file, encoding='utf-8'):
	ifile = open(in_file)
	ofile=  open(out_file, "w")
	for line in ifile:
		line = line.strip().decode(encoding)
		if line == '' or line[0] == "#":
			continue
		kv = line.split()
		code = kv[0]
		for i in xrange(1, len(kv)):
			print >> ofile, ("%s\t%s" % (kv[i], code)).encode(encoding)

if __name__ == "__main__":
	split(sys.argv[1], sys.argv[2], sys.argv[3])
	print "DONE!"