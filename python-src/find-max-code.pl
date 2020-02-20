#! /usr/bin/env perl
# -*- codingL utf-8 -*-

use strict;
use warnings;
use Encode;
#use Encode::CN;
#use Encode::HanExtra;
use utf8;
use 5.012;
use File::Basename;
use Getopt::Long;
my ($input, $output, $charset);
GetOptions(
    'input|i=s'   => \$input,
    'output|o=s'  => \$output,
    'charset|c=s' => \$charset);

die Encode::encode('GBK', "输入文件为空.") if (!defined $input);
if (!defined $output) {
    my @suffixlist = qw( .txt);
    my ($name, $path, $suffix) = fileparse($input, @suffixlist);
    $output = $path.$name.'-out'.$suffix;
}
if (!defined $charset) {
    $charset = 'utf-8';
}

open (INPUT, "<:encoding($charset)",  $input)
    || die("Could not open file $input with $charset");
my %dict = ();
while(<INPUT>){
    chomp;
    my @kv = split;
    my $hz = $kv[0];
    my $code = $kv[1];

    if(exists $dict{$hz}) {
        my $length_orig = length $dict{$hz};
        my $length_new = length $code;
        if ($length_orig < $length_new) {
            $dict{$hz} = $code;
        }
    } else {
        $dict{$hz} = $code;
    }
}
close(INPUT);

open (OUTPUT, ">:encoding(utf-8)",
    $output) || die("Could not open file $output with $charset");
while(my ($k, $v) = each %dict) {
    print OUTPUT "$k\t$v\n";
    # printf OUTPUT "%s\t%s\n", $k, $v;
}
say 'DONE!';

