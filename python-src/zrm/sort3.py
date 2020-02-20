#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys

from sort_order import sorted_lines


def usage():
    print '''usage:
    sys.argv[0] <in_file> <out_file> [encoding]

    '''


def comp(x, y):
    c = cmp(x['g'], y['g'])
    if c > 0:
        return 1
    elif c < 0:
        return -1
    else:
        c = cmp(x['v'], y['v'])
        if c > 0:
            return 1
        elif c < 0:
            return -1
        else:
            return cmp(x['score'], y['score'])


def load_sorted_table():
    lines = {}
    count = 1
    for line in sorted_lines:
        line = line.strip().decode(encoding)
        if line not in lines:
            lines[line] = count
            count += 1
    return lines


def load_data(input_file, encoding='utf-8'):
    data = list()
    fin = open(input_file)
    for line in fin:
        try:
            line = line.strip().decode(encoding)

            if line == '' or line[0] == '#':
                continue
            kv = line.split('\t')
            if kv[0] in lines:
                score = lines[kv[0]]
                m = {}
                m['k'] = kv[0]
                m['v'] = kv[1]
                if len(kv) == 3:
                    m['g'] = int(kv[2])
                else:
                    m['g'] = 0
                m['line'] = line
                m['score'] = score
                data.append(m)
            else:
                score = 100000
                m = {}
                m['k'] = kv[0]
                m['v'] = kv[1]
                if len(kv) == 3:
                    m['g'] = int(kv[2])
                else:
                    m['g'] = 0
                m['line'] = line
                m['score'] = score
                data.append(m)
            if m['v'][-1] not in 'abcdefghijklmnopqrstuvwxyz':
                m['v'] = m['v'][0:-1]
        except Exception, e:
            print e
    fin.close()
    return data


def save_sorted_data(output_file, encoding='utf-8'):
    fout = open(output_file, 'w')
    for e in data:
        print >> fout, e['line'].encode(encoding)
    fout.close()
    print 'Done!'


encoding = 'utf-8'
argc = len(sys.argv)
if argc == 1:
    usage()
    sys.exit(1)
elif argc == 2:
    f1 = sys.argv[1]
    (short_name, extension) = os.path.splitext(f1)
    f2 = '%s.sorted%s' % (short_name, extension)
elif argc == 3:
    f1 = sys.argv[1]
    f2 = sys.argv[2]
else:
    f1 = sys.argv[1]
    f2 = sys.argv[2]
    encoding = sys.argv[3]

lines = load_sorted_table()
data = load_data(f1, encoding)

# sort
#data.sort(key=lambda x: x['score'])
data.sort(comp)

save_sorted_data(f2, encoding)

# def comp(x, y):
#     if x < y:
#         return 1
#
# elif x > y:
# return -1
# else:
# return 0
#
# nums = [3, 2, 8, 0, 1]
# nums.sort(comp)
# print nums  # 降序排序[8, 3, 2, 1, 0]
# nums.sort(cmp)  # 调用内建函数cmp ，升序排序
# print nums  # 降序排序[0, 1, 2, 3, 8]
# 三、可选参数
# sort方法还有两个可选参数：key和reverse
#
# 1、key在使用时必须提供一个排序过程总调用的函数：
# x = ['mmm', 'mm', 'mm', 'm']
# x.sort(key=len)
# print x  # ['m', 'mm', 'mm', 'mmm']
# 2、reverse实现降序排序，需要提供一个布尔值：
# y = [3, 2, 8, 0, 1]
# y.sort(reverse=True)
# print y  # [8, 3, 2, 1, 0]
