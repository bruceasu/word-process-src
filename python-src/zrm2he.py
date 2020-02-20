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

mapping = {
        u'w': u'x',
        u'y': u'k',
        u'p': u'y',
        u'd': u'l',
        u'k': u'c',
        u'l': u'd',
        u'z': u'w', 
        u'x': u'p',
        u'c': u'n',
        u'b': u'z',
        u'n': u'b',
}

def convert_single(c, b):
    """"convert
    convert to he from zrm
    mapping 
    special rule
    n b if not start with a/e
    """
    if c == u'n':
        if b in u'ae':
            return c
        else:
            return u'b'
    elif c == u'r' and b == u'e':
        return c
    elif c in mapping:
        return mapping[c]
    else:
        return c

def convert(input, output, encoding=default_encoding):
   
    print 'convert from', input, 'to', output, '...'
    inFile = open(input)
    outFile = open(output, "w")
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
            if length_w == 1:
                if len(code) == 1:
                    new_code = code
                else:
                    new_code = code[0] + convert_single(code[1], code[0])
                if length_c > 2:
                    new_code += code[2:]
                #print w, code, new_code
                print >> outFile, ("%s\t%s" % (w, new_code)).encode(encoding)
            elif length_w == 2:
                if length_c == 2:
                    print >> outFile, ("%s\t%s" % (w, code)).encode(encoding)
                elif length_c == 3:
                    new_code = code[0] + convert_single(code[1], code[0]) + code[2]
                    print >> outFile, ("%s\t%s" % (w, new_code)).encode(encoding)
                else:
                    new_code = code[0] + convert_single(code[1], code[0]) + code[2] + convert_single(code[3], code[2])
                    print >> outFile, ("%s\t%s" % (w, new_code)).encode(encoding)
            elif length_w == 3:
                if length_c == 4:
                    new_code = code[0] + code[1] + code[2] + convert_single(code[3], code[2])
                    print >> outFile, ("%s\t%s" % (w, new_code)).encode(encoding)
                else:
                    print >> outFile, ("%s\t%s" % (w, code)).encode(encoding)
            else:
                print >> outFile, ("%s\t%s" % (w, code)).encode(encoding)
        except Exception, e:
            print e, line


        if count % 1000 == 0:
            print 'process %s words' % count
    inFile.close()
    outFile.close()
    print 'process %s words' % count

def sort_file(source_file, charset='utf-8'):
    dir_name = os.path.dirname(source_file)
    temp_file = os.path.join(dir_name, 'temp.txt')
    output = os.popen('sort.py %s %s %s' % (source_file, temp_file, charset))
    print output.read()
    import shutil
    shutil.move(temp_file, source_file)
    # open(source_file, "wb").write(open(temp_file, "rb").read())
    # os.remove(temp_file)
    
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
    sort_file(output)
    print "DONE!"

if __name__ == '__main__':
    main()
