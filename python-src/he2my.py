#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys


default_encoding = 'utf-8'


def usage():
    """ usage
    show usage.
    """
    print sys.argv[0], " <input> [output] [encoding]"

def load_mapping():
    mapping = {
        u'b': u'b',
        u'p': u'p',
        u'm': u'm',
        u'f': u'f',
        u'd': u'd',
        u't': u't',
        u'n': u'n',
        u'l': u'l',
        u'g': u'g',
        u'k': u'k',
        u'h': u'h',
        u'z': u'z',
        u'c': u'c',
        u's': u's',
        u'v': u'j',
        u'i': u'q',
        u'u': u'x',
        u'r': u'r',
        u'y': u'y',
        u'w': u'w',
        u'a': u'o',
        u'o': u'o',
        u'e': u'o',
    }
    f = open("he2my-mapping.txt")
    for line in f:
        line = line.strip().decode("utf-8")
        if line == "" or line[0] == '#':
            continue
        kv = line.split()
        mapping[kv[0]] = kv[1]
    f.close()
    return mapping

#def add_mapping():
#   @functools.wraps(func)
#   def wrapper(*args, **kw):
#        print('%s %s():' % (text, func.__name__))
#        return func(*args, **kw)
#   return wrapper

def add_mapping():
    mapping = load_mapping()
    def convert_syllable(s):
        """"convert syllable
        convert single syllable to my from he
        """
        if s in mapping:
            return mapping[s]
        else:
            return s
    return convert_syllable

def convert(input, output, encoding=default_encoding):
    """"convert
    convert to my from he
    """
    print 'convert from', input, 'to', output, '...'
    inFile = open(input)
    outFile = open(output, "w")
    convert_syllable = add_mapping()

    count = 0;
    for line in inFile:
        count += 1
        line = line.strip().decode(encoding)
        if line == '' or line[0] == '#':
            continue
        try:
            kv = line.split(None)
            if (len(kv) != 2):
                continue
            w = kv[0]
            code = kv[1]
            length_w = len(w)
            length_c = len(code)
            #print length_w, length_c
            if length_w == 1:
                if length_c == 1:
                    new_code = convert_syllable(code)
                else:
                    new_code = convert_syllable(code[0:2])
                if length_c > 2:
                    new_code += code[2:]
                #print w, code, new_code
                print >> outFile, ("%s\t%s" % (w, new_code)).encode(encoding)
            elif length_w == 2:
                #pass
                if length_c == 2:
                    print >> outFile, ("%s\t%s" % (w, convert_syllable(code))).encode(encoding)
                elif length_c == 3:
                    new_code = convert_syllable(code[0:2]) + convert_syllable(code[2])
                    print >> outFile, ("%s\t%s" % (w, new_code)).encode(encoding)
                else:
                    new_code = convert_syllable(code[0:2]) + convert_syllable(code[2:4])
                    print >> outFile, ("%s\t%s" % (w, new_code)).encode(encoding)
            elif length_w == 3:
                if length_c == 4:
                    new_code = convert_syllable(code[0]) + convert_syllable(code[1]) + convert_syllable(code[2:4])
                    print >> outFile, ("%s\t%s" % (w, new_code)).encode(encoding)
                else:
                    new_code = convert_syllable(code[0]) + convert_syllable(code[1]) + convert_syllable(code[2])
                    print >> outFile, ("%s\t%s" % (w, code)).encode(encoding)
            else:
                new_code = convert_syllable(code[0]) + convert_syllable(code[1]) + convert_syllable(code[2]) + convert_syllable(code[3])
                print >> outFile, ("%s\t%s" % (w, code)).encode(encoding)
        except Exception, e:
            print e, "line: ",count

        if count % 1000 == 0:
            print 'process %s words' % count
    inFile.close()
    outFile.close()
    print 'process %s words' % count


def main():
    length = len(sys.argv)
    if length == 1:
        usage()
        sys.exit(1)
    elif length == 2:
        input = sys.argv[1]
        (short_name, extension) = os.path.splitext(input)
        output = '%s.output%s' % (short_name, extension)
        encoding = default_encoding
    elif length == 3:
        input = sys.argv[1]
        output = sys.argv[2]
        encoding = default_encoding
    else:
        input = sys.argv[1]
        output = sys.argv[2]
        encoding = sys.argv[3]

    convert(input, output, encoding)

    print "DONE!"

if __name__ == '__main__':
    main()
