#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys
import mapping
import merge as m
#import make_short_for_zyoy as ms
import make_short_for_ding as ms

def sort_file(source_file, charset='utf-8'):
        #dir_name = os.path.dirname(source_file)
        #temp_file = os.path.join(dir_name, 'temp.txt')
        temp_file = 'temp.txt'
        output = os.popen('sort3.py %s %s %s' % (source_file, temp_file, charset))
        print output.read()
        import shutil
        shutil.move(temp_file, source_file)
        # open(source_file, "wb").write(open(temp_file, "rb").read())
        # os.remove(temp_file)


def find_file(base_name):
        in_data_dir = os.path.join('data', base_name)
        in_parent_data_dir = os.path.join('..', in_data_dir)
        # print base_name, in_data_dir, in_parent_data_dir
        if os.path.exists(base_name):
                return base_name
        elif os.path.exists(in_data_dir):
                return in_data_dir
        elif os.path.exists(in_parent_data_dir):
                return in_parent_data_dir
        else:
                raise "%s NOT FOUND!" % (base_name,)

# 拉丁字母
# b p m f d t n l g k ng h z c s j gw kw w 19/16
# aa a e o i u v eo/oe 9/8
# 天干地支
# 甲(a) 未(e) 丑(o) 子(i) 酉(u) 乙(ue) 戌(oe)
# 丙(b/p)  卯(m)  申(f)
# 丁(d/t) 壬(n) 辛(l)
# 庚(g/k) 午(ng) 亥(h)
# 己(z) 辰(c) 巳(s) 寅(y)
# 癸(gw/kw) 戊(w)

# 键盘布局1
# f m p b z ü u i o   ķ
# l n t d c ä a e ö/é ģ
# h ñ k g s y w , .   /

# 键盘布局1
# ㄈ ㄇ ㄆ ㄅ ㄗ ㄩ ㄨ ㄧ ㄛ ㄑ
# ㄌ ㄋ ㄊ ㄉ ㄘ ㄚ ㄒ ㄝ ㄟ ㄬ
# ㄏ ㄫ ㄎ ㄍ ㄙ 一 十 , . /

# ㄟ： 类似【ㄩㄝ】 月

# 键盘布局2
# ķ w e ä t y u i o p
# a s d f g n ñ k l ö
# z ģ c ü b h m , . /

# 键盘布局3 [十九键，辅音一码，元音一码]
#|----+----+----+-----+----+----+----+----+----+----|
#| ķ  | w  | ñ  | b   | t  | y  | m  | c  | ģ  | p  |
#| äi | ai | ei | öi  |    | ü  | u  | i  | o  | ou |
#|----+----+----+-----+----+----+----+----+----+----|
#| n  | s  | d  | f   | g  | h  | z  | k  | l  | ;  |
#| ä  | a  | e  | ö/ê | äu | au | ui | iu | oi |    |
#|----+----+----+-----+----+----+----+----+----+----|

# 键盘布局4 [辅音一码，元音一码，起首笔画一码]
# 横 竖 撇 捺 折 弯 叉 框
# 1  2  3  4  5  6  7  8
# h  s  f  d  j  l  k  g
#|---+------+------+------+------+------+------+------+------+---|
#| ķ | w    | e    | ä    | t    | y    | u    | i    | o    | p |
#|---+------+------+------+------+------+------+------+------+---|
#| a | s 竖 | d 捺 | f 撇 | g 框 | h 横 | ñ 折 | k 叉 | l 弯 | ; |
#|---+------+------+------+------+------+------+------+------+---|
#| z | ģ/ö/ê | c   | ü    | b    | n    | m    | ,    | .    | / |
#|---+-------+-----+------+------+------+------+------+------+---|


#轉寫：
#ķ Ķ = kw /kʷh/
#ģ Ģ = gw /kʷ/
#ñ Ñ = ng /ŋ/
#a = a  短a /ɐ/
#ä = ar 長a /a/
#ö = eu /œ/
#ê = eo /ø/
#ü = yu /y/
#y = y /j/
# 尖组
# z c s
# 团组
# g k h
# 照组字（翘舌音）
#ŝ = sh
#ĉ = ch
#ẑ = zh
# 精组字
#s = s
#c = c
#z = z
# 合并字母
#e = /e/ or /ɛ/
#i = /i/ or /ɪ/
#u = /u/ or /ʊ/
#éá

def get_mapping():
        s = {
                "": "",
                "b": "r",
                "p": "e",
                "m": "w",
                "f": "q",

                "d": "f",
                "t": "d",
                "n": "s",
                "l": "a",

                "g": "v",
                "k": "c",
                "~": "x",  # ng
                "h": "z",

                "z": "t",
                "c": "g",
                "s": "b",

                "j": "n",
                "w": "m",

                "#": ";",  # gw
                "$": "p"   # kw
        }
        w = {
                "": "",
                "a": "h",  # aa
                "r": "j",  # a
                "!": "l",  # eo	eo, eu是互补的。
                "@": "l",  # oe/eu

                "u": "u",  # 长u，于短u是互补的。
                "i": "i",  # 长i，于短i是互补的。
                "o": "o",
                "e": "k",
                "v": "y",  # yu
        }

        m = {
                "": "",
                "u": "u",
                "i": "i",
                "p": "e",
                "t": "d",
                "k": "c",
                "m": "w",
                "n": "s",
                "~": "x"  # ng
        }

        zyoy_mapping = {}
        for k1, v1 in s.items():
                for k2, v2 in w.items():
                        for k3, v3 in m.items():
                                zyoy_mapping["%s%s%s" % (k1, k2, k3)] = "%s%s%s" % (v1, v2, v3)
        return zyoy_mapping


def get_mapping2():
        s = {
                "": "",
                "b": "b",
                "p": "p",
                "m": "m",
                "f": "f",

                "d": "d",
                "t": "t",
                "n": "n",
                "l": "l",

                "g": "g",
                "k": "k",
                "~": "v",  # ng
                "h": "h",

                "z": "z",
                "c": "c",
                "s": "s",

                "j": "j",
                "w": "w",

                "#": "x",  # gw
                "$": "y",  # kw
        }
        w = {
                "": "-",
                "a": "a",  # aa
                "r": "q",  # a
                "e": "e",
                "v": "y",  # yu , 同u合并，kut,kvt, gun, gvn 会混，但方言中，两组音也是混的，关系不大。
                "u": "u",  # 长u，于短u是互补的。
                "i": "i",  # 长i，于短i是互补的。
                "o": "o",
                "!": "r",  # eo	eo, eu是互补的。
                "@": "r",  # oe/eu

                # 鼻音
                "an": "g",
                "rn": "f",
                "in": "d",
                "on": "s",
                "un": "c",
                "!n": "x",
                "vn": "z",

                "a~": "h",
                "r~": "j",
                "i~": "k",
                "o~": "l",
                "u~": "n",
                "@~": "m",
                "e~": "k",

                "am": "b",
                "rm": "w",
                "im": "p",

                # 双元音
                #"ai": "q",
                #"ri": "w",
                #"ei": "e",
                #"oi": "r",
                #"ui": "y",
                #"!i": "y",
                #"au": "r",
                #"ru": "t",
                #"iu": "u",
                #"ou": "p",

                # 入音
                #"ak" : "z",
                #"rk": "z",
                #"ok": "c",
                #"ek": "z",
                #"ik": "x",
                #"uk": "c",
                #"@k": "z",

                #"at" : "n",
                #"rt" : "n",
                #"ut" : "m",
                #"it" : "m",
                #"vt" : "m",
                #"!t" : "n",
                #"ot" : "m",

                #"ap": "b",
                #"rp": "b",
                #"ip": "b"
        }

        m = {
                "": "",
                "u": "u",
                "i": "i",
                "p": "p",
                "t": "t",
                "k": "k",
                #"m": "m",
                #"n": "n",
                #"~": "j"  # ng
        }

        zyoy_mapping = {}
        for k1, v1 in s.items():
                for k2, v2 in w.items():
                        for k3, v3 in m.items():
                                zyoy_mapping["%s%s%s" % (k1, k2, k3)] = "%s%s%s" % (v1, v2, v3)

        for (k, v) in zyoy_mapping.iteritems():
                if len(v)==2 and v[1] == '-':
                        zyoy_mapping[k] = 'o'+v[0]
        return zyoy_mapping


def get_mapping3():
        s = {
                u"": u"a",  # 零声母
                u"b": u"e",
                u"p": u"p",
                u"m": u";",
                u"f": u"f",

                u"d": u"d",
                u"t": u"t",
                u"n": u"r",
                u"l": u"l",

                u"g": u"g",
                u"k": u"k",
                u"ñ": u"o",  # ng
                u"~": u"o",  # ng
                u"h": u"h",

                u"z": u"j",
                u"c": u"q",
                u"s": u"s",
                u"j": u"y",
                
                u"w": u"w",
                u"ģ": u"u",  # gw
                u"ķ": u"i",  # kw
                u"#": u"u",  # gw
                u"$": u"i",  # kw

        }
        w = {
                u"": u"",
                u"a": u"a",  # aa
                u"r": u"s",  # a
                u"e": u"e",
                u"v": u"y",  # yu , 同u合并，kut,kvt, gun, gvn 会混，但方言中，两组音也是混的，关系不大。
                u"u": u"u",  # 长u，于短u是互补的。
                u"i": u"i",  # 长i，于短i是互补的。
                u"o": u"o",
                u"!": u"f",  # eo	eo, eu是互补的。
                u"@": u"f",  # oe/eu
                # 双元音
                "ai": "q",
                "ri": "w",
                "ei": "d",
                "oi": "l",
                "ui": "j",
                "!i": "r",
                "au": "t",
                "ru": "g",
                "iu": "k",
                "ou": "h",
        }

        m = {
                u"": u"",
                #u"u": u"u",
                #u"i": u"i",
                u"p": u"p",
                u"t": u"t",
                u"k": u"k",
                u"m": u";",
                u"n": u"r",
                u"ñ": u"o",  # ng
                u"~": u"o"  # ng
        }

        zyoy_mapping = {}
        for k1, v1 in s.items():
                for k2, v2 in w.items():
                        for k3, v3 in m.items():
                                zyoy_mapping["%s%s%s" % (k1, k2, k3)] = "%s%s%s" % (v1, v2, v3)
        return zyoy_mapping

def get_mapping4():
        s = {
                "": "",
                "b": "b",
                "p": "p",
                "m": "m",
                "f": "f",

                "d": "d",
                "t": "t",
                "n": "n",
                "l": "l",

                "g": "g",
                "k": "k",
                "~": "j",  # ng
                "h": "h",

                "z": "z",
                "c": "c",
                "s": "s",

                "j": "y",
                "w": "w",

                "#": "x",
                "$": "q",
        }
        w = {
                "": "",
                "a": "a",  # aa
                "r": "r",  # a
                "e": "e",
                "v": "v",  # yu , 同u合并，kut,kvt, gun, gvn 会混，但方言中，两组音也是混的，关系不大。
                "u": "u",  # 长u，于短u是互补的。
                "i": "i",  # 长i，于短i是互补的。
                "o": "o",
                "!": "x",  # eo	eo, eu是互补的。
                "@": "x",  # oe/eu

        }

        m = {
                "": "",
                "i": "i",
                "u": "u",
                "p": "p",
                "t": "d",
                "k": "k",

                "n": "n",
                "~": "j",  # ng
                "m": "m"
        }

        zyoy_mapping = {}
        for k1, v1 in s.items():
                for k2, v2 in w.items():
                        for k3, v3 in m.items():
                                key = "%s%s%s" % (k1, k2, k3)
                                value = "%s%s%s" % (v1, v2, v3)
                                zyoy_mapping[key] = value
        return zyoy_mapping


def make_mapping(single):
        """ 转换拼音
        把拼音转成键盘布局
        """
        py = "zyoy.single.mapped.txt"
        mapping.mapping(single, py, encoding, get_mapping3())
        return py


def merge(first, last_file='wubi.2codes.txt', charset="utf-8"):
        """ 合并辅助码
        """
        fisrt = find_file(first)
        last = find_file(last_file)
        print "last file: ", last
        merged_file = 'out/zyoy.single.merged.txt'
        m.merge(first, last, merged_file, encoding=charset)
        return merged_file


def make_short(merged_file, charset='utf-8'):
        """ 生成简码
        """
        ms.make_short(merged_file, charset)


if __name__ == '__main__':
        encoding = 'utf-8'

        # mapping
        single = find_file('cantonese-common.single.sort.txt')
        #single = find_file('cs2.txt')
        fisrt = make_mapping(single)

        # merge
        last_file= "data\\rain1.txt"
        #last_file= "data\\rain.txt"
        #last_file="data\kuaishou.txt"
        #last_file="data\\cj5.2codes.txt"
        #last_file="data\\abc.txt"
        merged = merge(fisrt, last_file, charset=encoding)
        # make short
        make_short(merged, encoding)
        sort_file('out\\zyoy.single.merged-out.txt')
        sort_file('out\\zyoy.single.merged-full.txt')
        sort_file('out\\zyoy.single.merged-ci.txt')

        print 'All DONE!'
