#!/usr/bin/env python
# -*- coding: utf-8 -*-

import logging
import sys
import os

def load_common_words():
	print "%s: Load common words." % sys.argv[0]
	common_file = open('data/common-words.txt')
	common_set = set()
	for line in common_file:
			line = line.strip().decode("utf-8")
			if line == "" or line[0] == '#':
					continue
			common_set.add(line)
	common_file.close()
	print "common words loaded."
	return common_set

def main():
	infile = 'out/hyly.single.merged-out.txt'
	outfile1 = 'out/hyly.single.merged-ci.txt'
	outfile2 = 'out/hyly.single.merged-other.txt'
	encoding = 'utf-8'
	common_set = load_common_words()
	f1 = open(infile)
	f2 = open(outfile1, "w")
	f3 = open(outfile2, "w")
	for line in f1:
		line = line.strip().decode("utf-8")
		if line == "" or line[0] == "#":
			continue
		kv = line.split()
		if kv[0] in common_set:
			print >> f2, line.encode("utf-8")
		else:
			print >> f3, line.encode("utf-8")
	f1.close()
	f2.close()
	f3.close()
	print('%s: DONE!' % sys.argv[0])


if __name__ == '__main__':
	main()
