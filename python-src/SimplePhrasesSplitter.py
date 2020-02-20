#!/usr/bin/env python
# -*- coding:utf-8 -*-

# 写了一个简单的支持中文的正向最大匹配的机械分词,其它不用解释了，就几十行代码

import string

class SimplePhrasesSplitter:
    def __init__(self):
        self.__dict = {}

    def __init__(self, dict_file=None):
        self.__dict = {}
        if dict_file:
            self.load_dict(dict_file)

    def load_dict(self, dict_file='words.dic'):
        # 加载词库，把词库加载成一个key为首字符，value为相关词的列表的字典

        phrases = [unicode(line.strip(), 'utf-8') for line in open(dict_file)]

        for phrase in phrases:
            if phrase == '' or phrase[0] == '#':
                continue
            first_char = phrase[0]
            self.__dict.setdefault(first_char, [])
            self.__dict[first_char].append(phrase)

        # 按词的长度倒序排列
        for first_char, words in self.__dict.items():
            self.__dict[first_char] = sorted(words, key=lambda x: len(x), reverse=True)

    def add_phrase_to_dict(self, phrase):
        first_char = phrase[0]
        self.__dict.setdefault(first_char, [])
        self.__dict[first_char].append(phrase)
        # 按词的长度倒序排列
        self.__dict[first_char] = sorted(self.__dict[first_char],
                                         key=lambda x: len(x),
                                         reverse=True)

    def add_phrases_to_dict(self, phrases):
        for phrase in phrases:
            first_char = phrase[0]
            self.__dict.setdefault(first_char, [])
            self.__dict[first_char].append(phrase)

            # 按词的长度倒序排列
            self.__dict[first_char] = sorted(self.__dict[first_char],
                                             key=lambda x: len(x),
                                             reverse=True)

    def __match_ascii(self, i, input_text):
        # 返回连续的英文字母，数字，符号
        result = ''
        for i in range(i, len(input_text)):
            if not input_text[i] in string.ascii_letters:
                break
            result += input_text[i]
        return result

    def __match_word(self, first_char, i, input_text):
        # 根据当前位置进行分词，ascii的直接读取连续字符，中文的读取词库
        if not self.__dict.has_key(first_char):
            if first_char in string.ascii_letters:
                return self.__match_ascii(i, input_text)
            return first_char

        words = self.__dict[first_char]
        for word in words:
            # print word, " vs ", input_text[i:i+len(word)]

            if input_text[i:i+len(word)] == word:
                return word

        return first_char

    def tokenize(self, input_text):
        # 对 input_text 进行分词，input_text 必须是 unicode 编码
        if not input_text:
            return []

        tokens = []
        i = 0
        while i < len(input_text):
            first_char = input_text[i]
            matched_word = self.__match_word(first_char, i, input_text)
            tokens.append(matched_word)
            i += len(matched_word)

        return tokens

    def print_phrases(self, fitler):
        if fitler:
            for first_char, words in self.__dict.items():
                if first_char == fitler:
                    try:
                        print '%s:%s' % (first_char, ' '.join(words))
                    except Exception, e:
                        pass
        else:
            for first_char, words in self.__dict.items():
                try:
                    print '%s:%s' % (first_char, ' '.join(words))
                except Exception, e:
                    pass


if __name__ == '__main__':
    def get_test_text():
        import urllib2
        url = "http://news.baidu.com/n?cmd=4&class=rolling&pn=1&from=tab&sub=0"
        text = urllib2.urlopen(url).read()
        return unicode(text, 'gbk')

    def load_dict_test():
        splitter = SimplePhrasesSplitter('../data/phrases.min.txt')
        splitter.print_phrases(u'花')


    def tokenize_test(text):
        splitter = SimplePhrasesSplitter('../data/phrases.min.txt')
        tokens = splitter.tokenize(text)
        for token in tokens:
            print token
    # load_dict_test()
    tokenize_test(unicode(u'美丽的花园里有各种各样的小动物'))
    tokenize_test(u'高不成低不就')

    # tokenize_test(get_test_text())
