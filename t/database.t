#!/usr/bin/env perl

use 5.010;

use strict;
use warnings;

use DBI;
use Test::More;

my $synth_db = DBI->connect(
	"dbi:SQLite:dbname=assets/synths.sqlite", "", "",
);

isa_ok($synth_db, 'DBI::db', 'SQLite database');

my $makers = get_db_hash($synth_db, qq~
	SELECT * FROM makers
~, "_id");

foreach my $id (keys %$makers) {
	my $maker = $makers->{$id};
	$maker->{re} = '^'.$maker->{re}.'$';
	my $re = qr/$maker->{re}/;
	like(
		lc($maker->{maker}), $re, 
		"maker $maker->{maker}: name matches",
	);
	unlike(
		'zaphodbeeblebrox', $re, 
		"maker $maker->{maker}: random string doesn't match",
	);
}

my $synths = get_db_hash($synth_db, qq~
	SELECT * FROM synths
~, "_id");

foreach my $id (keys %$synths) {
	my $synth = $synths->{$id};
	$synth->{re} = '^'.$synth->{re}.'$';
	my $re = qr/$synth->{re}/;
	like(
		lc($synth->{model}), $re, 
		"synth $synth->{model}: name matches",
	);
	unlike(
		'zaphodbeeblebrox', $re, 
		"maker $synth->{model}: random string doesn't match",
	);
}

foreach my $id (keys %$synths) {
	ok(
		exists $makers->{$synths->{$id}->{maker_id}},
		"synth $id has maker defined",
	);
}

my $quizzes = get_db_hash($synth_db, qq~
	SELECT * FROM quizzes
~, "_id");

foreach my $id (keys %$quizzes) {
	my $quiz = $quizzes->{$id};
	ok(
		exists $synths->{$quiz->{synth_id}},
		"quiz $id has synth defined",
	);
}

sub get_db_hash {
	my($dbh, $query, $key) = @_;
	my $sth = $synth_db->prepare($query);
	$sth->execute() or die "can't execute query: $query";
	my $hashref = $sth->fetchall_hashref($key);
}

done_testing();
