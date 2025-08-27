#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys

default_encoding = 'utf-8'

def usage():
    """ usage
    show usage.
    """
    print(sys.argv[0], " <input> [output] [encoding]")

mapping = {
    'w': 'x',
    'y': 'k',
    'p': 'y',
    'd': 'l',
    'k': 'c',
    'l': 'd',
    'z': 'w', 
    'x': 'p',
    'c': 'n',
    'b': 'z',
    'n': 'b',
}

def convert_single(c, b):
    """"convert
    convert to he from zrm
    mapping 
    special rule
    n b if not start with a/e
    """
    if c == 'n':
        if b in 'ae':
            return c
        else:
            return 'b'
    elif c == 'r' and b == 'e':
        return c
    elif c in mapping:
        return mapping[c]
    else:
        return c

def convert(input, output, encoding=default_encoding):
    print('convert from', input, 'to', output, '...')
    
    with open(input, 'r', encoding=encoding) as inFile, open(output, 'w', encoding=encoding) as outFile:
        count = 0
        for line in inFile:
            count += 1
            line = line.strip()
            if line == '' or line[0] == '#':
                continue
            try:
                kv = line.split(None)
                if len(kv) != 2:
                    continue
                w = kv[1]
                code = kv[0]
                length_w = len(w)
                length_c = len(code)
                
                if length_w == 1:
                    if len(code) == 1:
                        new_code = code
                    else:
                        new_code = code[0] + convert_single(code[1], code[0])
                    if length_c > 2:
                        new_code += code[2:]
                    print(f"{w}\t{new_code}", file=outFile)
                
                elif length_w == 2:
                    if length_c == 2:
                        print(f"{w}\t{code}", file=outFile)
                    elif length_c == 3:
                        new_code = code[0] + convert_single(code[1], code[0]) + code[2]
                        print(f"{w}\t{new_code}", file=outFile)
                    else:
                        new_code = code[0] + convert_single(code[1], code[0]) + code[2] + convert_single(code[3], code[2])
                        print(f"{w}\t{new_code}", file=outFile)
                
                elif length_w == 3:
                    if length_c == 4:
                        new_code = code[0] + code[1] + code[2] + convert_single(code[3], code[2])
                        print(f"{w}\t{new_code}", file=outFile)
                    else:
                        print(f"{w}\t{code}", file=outFile)
                else:
                    print(f"{w}\t{code}", file=outFile)
            
            except Exception as e:
                print(e, line)

            if count % 1000 == 0:
                print(f'process {count} words')

    print(f'process {count} words')

def sort_file(source_file, charset='utf-8'):
    dir_name = os.path.dirname(source_file)
    temp_file = os.path.join(dir_name, 'temp.txt')
    output = os.popen(f'sort.py {source_file} {temp_file} {charset}')
    print(output.read())
    #import shutil
    #shutil.move(temp_file, source_file)

def main():
    length = len(sys.argv)
    if length == 1:
        usage()
        sys.exit(1)
    elif length == 2:
        input = sys.argv[1]
        short_name, extension = os.path.splitext(input)
        output = f'{short_name}.output{extension}'
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
    print("DONE!")

if __name__ == '__main__':
    main()
