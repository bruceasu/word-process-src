#! /usr/bin/env perl
# -*- coding: utf-8 -*-
# join
# code word1
# code word2
# to code word1 word2
# 
use strict;
use warnings;
use Encode;
#use Encode::HanExtra;
use Encode::CN;
use utf8;
use Getopt::Long;
use 5.010;

my ($input, $output, $encode);
GetOptions(
    'input|i=s'  => \$input,
    'output|o=s' => \$output,
    'encode|e=s' => \$encode);

if (!defined $encode) {
    $encode = 'utf-8';
}

open (INPUT, "<:encoding($encode)",
    $input) || die("Could not open file $input with $encode");
my %d = { };
while(<INPUT>){
    chomp;
    my @kv = split /\s+/, $_;
    push (@{$d{$kv[1]}}, $kv[0]);
}
close(INPUT);

if (defined $output) {
    open (OUTPUT, ">:encoding($encode)",
        $output) || die("Could not open file $output with $encode");
    select OUTPUT;
}
foreach my $key (sort keys %d) {
    if (defined $d{$key}) {
          printf "%s\t%s\n", $key, (join ' ',  @{$d{$key}});
    }
}
close(OUTPUT);
select STDOUT;

#GetOptions(
# 'verbose+'  => \$verbose, # 接有 + 的选项不接收变量,后面不需要加内容.
# 直接使用就行了,会在每次出现时增加一次变量,
# 就是讲命行时在参数中 -verbose -verbose
# 出现二次时 verbose 的值就会变成 2.
#'more!'     => \$more,     # 接有 ! 的选项不接收变量
# (也就是讲后面不需要加参数 –more 来使用就行了),
# 只要命令行中出现了这个参数,就会默认是 1 ,
# 是用来设置打开和关掉一个功能的.
# 可以在参数前加 no 变成负的例如-nomore.
#'debug:i'   => \$debug,    # 接有 = 的字符串要求接字符串（s）、整数（i）,或者浮点（f）等类型的变量.
#'lib=s'     => \@libs,     # 如果相关联的变量是个数组, 如这个地方的 @libs,
# 那么选项可以多次出现, 值可以被推到数组里.
# --lib="aaa" --lib="bbb" --lib "cccc"
#'flag=s'    => \%flags,    # 如果相关联的变量是个散列,
# 那么就要求一个键=值（key=value）对,
# 并被插入到散列里.
# --flag a=1 --flag b=2
#'all|everything|universe' => $all,
# # 别名
#'input|i=s'  => \$input, #  接有 | 的选项表示可以给 –test 简写为 -t.
#'output|o=s' => \$output,
#'encode|e=s' => \$encode,
#);

# $str = Encode::decode("utf8", $str);
# $str = Encode::encode("utf8", $str);
# from_to(my $utf8 = $gbk, 'GB18030', 'UTF-8')