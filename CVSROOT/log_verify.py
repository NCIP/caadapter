#!/usr/bin/python

import sys

def main(args):
	tmp = open("/tmp/loginfo.tmp", "w")
	tmp.write("%s" % '---------------------------------------------------------------')
	
	for arg in args:
		tmp.write("%s\t" % arg)
		
	tmp.write('\n')
	tmp.close()
	
	return 0

if __name__ == '__main__':	
	sys.exit(main(sys.argv[1:]))
