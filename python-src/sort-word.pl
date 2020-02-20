#! /usr/bin/env perl
# -*- codingL utf-8 -*-

use strict;
use warnings;
use utf8;
use 5.012;
use File::Basename;

die "please input the file name." if (@ARGV == 0);

my $f1 = $ARGV[0];

open (INPUT, "<:encoding(utf-8)", '../data/sort-order.txt')
    || die("Could not open file ../data/sort-order.txt with utf-8");
my %score = ();
my $cnt = 1;
while(<INPUT>){
    chomp;
    $score{$_} = $cnt++ unless (exists $score{$_});
}
close(INPUT);

open (INPUT, "<:encoding(utf-8)",
    $f1) || die("Could not open file $f1 with utf-8");
my @data = ();
while(<INPUT>){
    chomp;
    if (/^\s+$|^#/) {
        next;
    }
    my @kv = split;
    my $hz = shift @kv;
    if (exists  $score{$hz}) {
        my %m = (
            k     => $hz,
            line  => $_,
            score => $score{$hz}
        );
        push @data, \%m;
    } else {
        my %m = (
            k     => $hz,
            line  => $_,
            score => 1000000
        );
        push @data, \%m;
    }
}
close(INPUT);
my @list = sort {$a->{score} <=> $b->{score}} @data;

my @suffixlist = qw( .txt);
my ($name, $path, $suffix) = fileparse($f1, @suffixlist);
my $f2 = $path.$name.'-sorted'.$suffix;
open (OUTPUT, ">:encoding(utf-8)", $f2)
    || die("Could not open file $f2 with utf-8");
foreach my $n (@list) {
    say OUTPUT $n->{"line"};
}
close(OUTPUT);
say 'DONE!';

