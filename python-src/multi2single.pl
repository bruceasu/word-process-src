#! /usr/bin/env perl
# -*- coding: utf-8 -*-
# split
# code word1 word2
# to
# code word1
# code word2

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

if (defined $output) {
    open (OUTPUT, ">:encoding($encode)",
        $output) || die("Could not open file $output with $encode");
    select OUTPUT;
}
while(<INPUT>){
    chomp;
    my @kv = split /\s+/, $_;
    my $hz = shift @kv;
    while(my $code = shift @kv) {
        printf "%s\t%s\n", $hz, $code;
    }
}
close(INPUT);
close(OUTPUT);
select STDOUT;
