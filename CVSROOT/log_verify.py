#!/usr/bin/python

# We expect comments to be of the form GF0000 : <comment text>
#
# The length of <comment text> can not be zero or all blanks

import sys

MIN_COMMENT_LENGTH = 5

def main(args):
	log = open(args[0], "r");
	
	log_comment = ' '.join(log)
	log_comment = log_comment.splitlines()
	log_comment = ' '.join(log_comment)	
	log_comment = log_comment.strip()
	
	log.close()
		
	""" comment must begin with 'GF' (case insensitive) """
	if not log_comment.upper().startswith('GF'):
#		print "log comment does not start with 'GF'"
		return 1
		
	contents = log_comment.split(':')
#	print contents
	
	""" comment line must include a colon and non-zero length comment after the colon """
	if len(contents) < 2:
#		print "log comment does not include colon"
		return 1
		
	""" get the numeric part of the GF tracker identifier (everything except the leading 'GF') """
	gf_number = contents[0][2:].strip()
	
	""" reconstruct the comment text as a single string """
	comment_text = "".join(contents[1:])	
	
	if not gf_number.isdigit():
#		print "GF tracker number not numeric: [%s]" % gf_number
		return 1

	if not len(comment_text.strip()) > MIN_COMMENT_LENGTH:	
#		print "comment text not greater than pre-defined minimum"
		return 1
	
	return 0

if __name__ == '__main__':	
	sys.exit(main(sys.argv[1:]))
#	retval = main(sys.argv[1:])
#	print retval
#	sys.exit(retval)
		
