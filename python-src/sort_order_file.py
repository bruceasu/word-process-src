#!/usr/bin/env python
# -*- coding: utf-8 -*-
import os

__author__ = 'Suk Honzeon'


class Dic:
    """ 排序字典

    """
    def __init__(self, simplified=True, encoding='utf-8'):
        self.encoding = encoding
        self.simplified = simplified
        self.dic = self.load_dict()

    @staticmethod
    def open_sort_dict_s():
        txt = 'sort-order.txt'
        order_txt = os.path.join('data', txt)
        sort_order_txt = os.path.join('..', order_txt)
        if os.path.exists(txt):
            dic_file = open(txt)
        elif os.path.exists(order_txt):
            dic_file = open(order_txt)
        elif os.path.exists(sort_order_txt):
            dic_file = open(sort_order_txt)
        else:
            raise "sort-order.txt NOT FOUND!"
        return dic_file

    @staticmethod
    def open_sort_dict_t():
        txt = 'sort-order-t.txt'
        order_txt = os.path.join('data', txt)
        sort_order_txt = os.path.join('..', order_txt)
        if os.path.exists(txt):
            dic_file = open(txt)
        elif os.path.exists(order_txt):
            dic_file = open(order_txt)
        elif os.path.exists(sort_order_txt):
            dic_file = open(sort_order_txt)
        else:
            raise "sort-order.txt NOT FOUND!"
        return dic_file

    def load_dict(self):
        if self.simplified:
            dic_file = self.open_sort_dict_s()
        else:
            dic_file = self.open_sort_dict_t()
        lines = {}
        count = 1
        for dic in dic_file:
            dic = dic.strip().decode(self.encoding)
            if dic not in lines:
                lines[dic] = count
                count += 1
        dic_file.close()
        return lines

    def get_score(self, word):
        if word in self.dic:
            return self.dic[word]
        else:
            return 100000
