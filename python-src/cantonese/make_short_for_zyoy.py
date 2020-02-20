#!/usr/bin/env python
# -*- coding: utf-8 -*-

import logging
import sys
import os


def load_common_words():
		print "%s: Load common words." % sys.argv[0]
		common_file = open('data/500.txt')
		common_set = set()
		for line in common_file:
				line = line.strip().decode("utf-8")
				if line == "" or line[0] == '#':
						continue
				common_set.add(line)
		common_file.close()
		print "common words loaded."
		return common_set

def load_common_words2():
		print "%s: Load common words." % sys.argv[0]
		common_file = open('data/common-words-1600.txt')
		common_set = set()
		for line in common_file:
				line = line.strip().decode("utf-8")
				if line == "" or line[0] == '#':
						continue
				common_set.add(line)
		common_file.close()
		print "common words 2 loaded."
		return common_set

def load_common_words3():
		print "%s: Load common words." % sys.argv[0]
		common_file = open('data/gb2312-1.txt')
		common_set = set()
		for line in common_file:
				line = line.strip().decode("utf-8")
				if line == "" or line[0] == '#':
						continue
				common_set.add(line)
		common_file.close()
		print "common words 3 loaded."
		return common_set




def load_common_words4():
		print "%s: Load common words." % sys.argv[0]
		common_file = open('data/common-words.txt')
		common_set = set()
		for line in common_file:
				line = line.strip().decode("utf-8")
				if line == "" or line[0] == '#':
						continue
				common_set.add(line)
		common_file.close()
		print "common words 4 loaded."
		return common_set

def make_one_set():
	one_set = {}

	one_set[u'啊'] = u'a'
	#one_set[u'阿'] = u'a;'
	#one_set[u'乸'] = u'a,'

	one_set[u'不'] = u'b'
	#one_set[u'把'] = u'b;'
	#one_set[u'畀'] = u'b,'

	one_set[u'出'] = u'c'
	#one_set[u'产'] = u'c;'
	#one_set[u'前'] = u'c,'

	one_set[u'的'] = u'd'
	#one_set[u'地'] = u'd;'
	#one_set[u'得'] = u'd,'

	one_set[u'咩'] = u'e'
	#one_set[u'奀'] = u'e;'
	#one_set[u'欸'] = u'e,'

	one_set[u'非'] = u'f'
	#one_set[u'方'] = u'f;'
	#one_set[u'发'] = u'f,'

	one_set[u'个'] = u'g'
	#one_set[u'工'] = u'g;'
	#one_set[u'经'] = u'g,'

	one_set[u'可'] = u'h'
	#one_set[u'下'] = u'h;'
	#one_set[u'去'] = u'h,'
	#one_set[u'喺'] = u'h.'
	
	one_set[u'出'] = u'i'
	#one_set[u'哋'] = u'i;'
	#one_set[u'啲'] = u'i,'

	one_set[u'我'] = u'j'
	#one_set[u'啱'] = u'j;'
	#one_set[u'嗯'] = u'j,'

	one_set[u'其'] = u'k'
	#one_set[u'强'] = u'k;'
	#one_set[u'佢'] = u'k,'

	one_set[u'了'] = u'l'
	#one_set[u'力'] = u'l;'
	#one_set[u'里'] = u'l,'

	one_set[u'唔'] = u'm'
	#one_set[u'冇'] = u'm;'
	#one_set[u'乜'] = u'm,'
	#one_set[u'民'] = u'm.'
	
	one_set[u'你'] = u'n'
	#one_set[u'您'] = u'n;'
	#one_set[u'能'] = u'n,'
	
	one_set[u'安'] = u'o'
	#one_set[u'哦'] = u'o;'
	#one_set[u'奧'] = u'o,'

	one_set[u'被'] = u'p'
	#one_set[u'便'] = u'p;'
	#one_set[u'平'] = u'p,'

	one_set[u'群'] = u'q'
	#one_set[u'狂'] = u'q;'
	#one_set[u'框'] = u'q,'

	one_set[u'暗'] = u'r'
	#one_set[u'握'] = u'r;'
	#one_set[u'呕'] = u'r,'

	one_set[u'小'] = u's'
	#one_set[u'上'] = u's;'
	#one_set[u'时'] = u's,'
	#one_set[u'事'] = u's.'
	
	one_set[u'同'] = u't'
	#one_set[u'他'] = u't;'
	#one_set[u'她'] = u't,'
	#one_set[u'它'] = u't.'

	one_set[u'是'] = u'u'
	#one_set[u'屋'] = u'u'
	#one_set[u'蕹'] = u'u;'
	#one_set[u'甕'] = u'u,'

	#one_set[u'主'] = u'v'
	one_set[u'这'] = u'v'
	
	one_set[u'为'] = u'w'
	#one_set[u'位'] = u'w;'
	#one_set[u'和'] = u'w,'
	#one_set[u'回'] = u'w.'
	
	one_set[u'国'] = u'x'
	#one_set[u'过'] = u'x;'
	#one_set[u'果'] = u'x,'

	one_set[u'一'] = u'y'
	#one_set[u'人'] = u'y;'
	#one_set[u'有'] = u'y,'
	#one_set[u'嘢'] = u'y.'
	
	#one_set[u'中'] = u'z'
	one_set[u'在'] = u'z'
	#one_set[u'这'] = u'z,'

	return one_set

def sort_file(source_file, charset='utf-8'):
	""" 排序
	:param source_file: 原始文件
	:param charset:  文件编码
	:return: nil
	"""
	dir_name = os.path.dirname(source_file)
	temp_file = os.path.join(dir_name, 'temp.txt')
	output = os.popen('sort3.py %s %s %s' % (source_file, temp_file, charset))
	print output.read()
	import shutil
	shutil.move(temp_file, source_file)
	# open(source_file, "wb").write(open(temp_file, "rb").read())
	# os.remove(temp_file)


def make_short(infile, coding='utf-8'):
	"""
		1. 预设的一简
		2. 前500字全部有音节简。
		3. 先处理前1600字，如果有空音节简的，填到音节简中，如果1个辅助简不超过2个，可以填充1个辅助简。
		4. 再处理前3700字，如果无1个辅助简的，填到1个辅助简中。
		5. 其它的只有全码，超过5000的字设置为生僻字。
		"""
	(short_name, extension) = os.path.splitext(infile)
	out_file = '%s-out%s' % (short_name, extension)
	out_full_file = '%s-full%s' % (short_name, extension)
	out_ci_file = '%s-ci%s' % (short_name, extension)

	input_file = open(infile)
	output_file = open(out_file, "w")
	output_full_file = open(out_full_file, "w")
	output_ci_file = open(out_ci_file, "w")

	print "%s: begin to process %s." % (sys.argv[0], infile)

	one_set = make_one_set()
	common_set  = load_common_words()
	common_set2 = load_common_words2()
	common_set3 = load_common_words3()
	common_set4 = load_common_words4()

	code_set={}
	result = []
	full = []
	code_len_count = []
	for i in range(0, 30):
		code_len_count.append(0)

	remain = {}
	sp = []
	ci = set()
	count = 0
	padding = ['', ';', ',', '.', '/', '\'']
	for k, v in one_set.iteritems():
		if v in code_set:
			code_set[v] += 1
		else:
			code_set[v] = 1
		code_len_count[1] += 1
		result.append(k + "\t" + v + "\t1")

	set_500 = []
	set_1600 = []
	set_3700 = []
	set_5000 = []
	set_other = []
	for line in input_file:
		count += 1
		line = line.strip().decode(coding)
		if line == "" or line[0] == '#':
			continue

		kv = line.split(None)
		w = kv[0]
		c = kv[1]
		cc = c.split(':')
		key_single = cc[0][0]
		key_syllable = cc[0]
		key_ext = cc[0] + cc[1][0]
		ci.add(w + '\t' + cc[0] + '\n')
		cc_full = cc[0] + cc[1]

		if w in one_set:
			full.append(w + "\t" + cc_full)
		elif w in common_set:
			set_500.append((w, cc[0], cc[1]))
		elif w in common_set2:
			set_1600.append((w, cc[0], cc[1]))
		elif w in common_set3:
			set_3700.append((w, cc[0], cc[1]))
		elif w in common_set4:
			set_5000.append((w, cc[0], cc[1]))
		else:
			if cc_full not in code_set:
				code_set[cc_full] = 1
			else:
				code_set[cc_full] += 1
			code_len_count[len(cc_full)] += 1
			sp.append(w + "\t" + cc_full)

	for (w, k1, k2) in set_500:
		key_syllable = k1
		key_ext = k1 + k2[0]
		cc_full = k1 + k2
		if key_syllable not in code_set:
			code_set[key_syllable] = 1
			code_len = len(key_syllable)
			code_len_count[code_len] += 1
			result.append(w + "\t" + key_syllable + "\t2")
			full.append(w + "\t" + cc_full)
		elif code_set[key_syllable] < 2:
			new_code = key_syllable + padding[code_set[key_syllable]]
			code_len = len(new_code)
			if new_code in code_set:
				code_set[new_code] += 1
			else:
				code_set[new_code] = 1
			code_set[key_syllable] += 1
			code_len_count[code_len] += 1
			result.append(w + "\t" + new_code + "\t3")
			full.append(w + "\t" + cc_full)
		elif key_ext not in code_set:
			code_set[key_ext] = 1
			code_len = len(key_ext)
			code_len_count[code_len] += 1
			result.append(w + "\t" + key_ext + "\t4")
			full.append(w + "\t" + cc_full)
		elif cc_full not in code_set:
			code_set[cc_full] = 1
			code_len = len(cc_full)
			code_len_count[code_len] += 1
			result.append(w + "\t" + cc_full + "\t5")
		else:
			code_set[cc_full] += 1
			code_len = len(cc_full)
			code_len_count[code_len] += 1
			result.append(w + "\t" + cc_full + "\t6")
	for (w, k1, k2) in set_1600:
		key_syllable = k1
		key_ext = k1 + k2[0]
		cc_full = k1 + k2
		if key_syllable not in code_set:
			code_set[key_syllable] = 1
			code_len = len(key_syllable)
			code_len_count[code_len] += 1
			result.append(w + "\t" + key_syllable + "\t3")
			full.append(w + "\t" + cc_full)
		elif key_ext not in code_set:
			code_set[key_ext] = 1
			code_len = len(key_ext)
			code_len_count[code_len] += 1
			result.append(w + "\t" + key_ext + "\t5")
			full.append(w + "\t" + cc_full)
		#elif key_ext in code_set and code_set[key_ext] < 2:
		#	new_code = key_ext + padding[code_set[key_ext]]
		#	if new_code in code_set:
		#		code_set[new_code] += 1
		#	else:
		#		code_set[new_code] = 1
		#	code_set[key_ext] += 1
		#	code_len = len(new_code)
		#	code_len_count[code_len] += 1
		#	result.append(w + "\t" + new_code + "\t6")
		#	full.append(w + "\t" + cc_full)
		elif cc_full not in code_set:
			code_set[cc_full] = 1
			code_len = len(cc_full)
			code_len_count[code_len] += 1
			result.append(w + "\t" + cc_full + "\t7")
		else:
			code_set[cc_full] += 1
			code_len = len(cc_full)
			code_len_count[code_len] += 1
			result.append(w + "\t" + cc_full + "\t8")
	for (w, k1, k2) in set_3700:
		key_syllable = k1
		key_ext = k1 + k2[0]
		cc_full = k1 + k2
		if key_syllable not in code_set:
			code_set[key_syllable] = 1
			code_len = len(key_syllable)
			code_len_count[code_len] += 1
			result.append(w + "\t" + key_syllable + "\t4")
			full.append(w + "\t" + cc_full)
		elif key_ext not in code_set:
			code_set[key_ext] = 1
			code_len = len(key_ext)
			code_len_count[code_len] += 1
			result.append(w + "\t" + key_ext + "\t6")
			full.append(w + "\t" + cc_full)
		elif cc_full not in code_set:
			code_set[cc_full] = 1
			code_len = len(cc_full)
			code_len_count[code_len] += 1
			result.append(w + "\t" + cc_full + "\t7")
		else:
			code_set[cc_full] += 1
			code_len = len(cc_full)
			code_len_count[code_len] += 1
			result.append(w + "\t" + cc_full + "\t8")
	for (w, k1, k2) in set_5000:
		key_syllable = k1
		key_ext = k1 + k2[0]
		cc_full = k1 + k2
		if key_syllable not in code_set:
			code_set[key_syllable] = 1
			code_len = len(key_syllable)
			code_len_count[code_len] += 1
			result.append(w + "\t" + key_syllable + "\t5")
			full.append(w + "\t" + cc_full)
		elif key_ext not in code_set:
			code_set[key_ext] = 1
			code_len = len(key_ext)
			code_len_count[code_len] += 1
			result.append(w + "\t" + key_ext + "\t6")
			full.append(w + "\t" + cc_full)
		elif cc_full not in code_set:
			code_set[cc_full] = 1
			code_len = len(cc_full)
			code_len_count[code_len] += 1
			result.append(w + "\t" + cc_full + "\t7")
		else:
			code_set[cc_full] += 1
			code_len = len(cc_full)
			code_len_count[code_len] += 1
			result.append(w + "\t" + cc_full + "\t8")

	print '%s: processed %s words' % (sys.argv[0], count)
	input_file.close()
	print ("%s: %d words. And %d codes." % (sys.argv[0], (len(result) +len(sp)), len(code_set)))

	for line in full:
		kv = line.split()
		if kv[1] not in code_set:
			code_set[kv[1]] = 1
			result.append(line + "\t20")
		else:
			code_set[kv[1]] += 1
			result.append(line + "\t30")
		code_len = len(kv[1])
		code_len_count[code_len] += 1

	for line in sp:
		print >> output_full_file, line.encode(coding)

	output_full_file.close()

	for line in result:
		print >> output_file, line.encode(coding)
	output_file.close()

	for line in ci:
		print >> output_ci_file, line.encode(coding)
	output_ci_file.close()

	print "%s: Output to %s and %s" % (sys.argv[0], out_file, out_full_file)
	print "%s: All %d words. And %d codes." % (sys.argv[0], len(result) + len(sp) , len(code_set))
	print "%s: Code Length Count: %s" % (sys.argv[0], code_len_count)

def main():
	infile = 'out/zyoy.single.merged.txt'
	encoding = 'utf-8'
	length = len(sys.argv)
	if length > 2:
		infile = sys.argv[1]
		encoding = sys.argv[2]
	elif length > 1:
		infile = sys.argv[1]

	make_short(infile, encoding)
	print('%s: DONE!' % sys.argv[0])


if __name__ == '__main__':
	main()
