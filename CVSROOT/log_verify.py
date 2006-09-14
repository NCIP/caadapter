#!/usr/bin/python

import sys

def main(args):
	tmp = open("/tmp/loginfo.tmp", "w")
	log = open(args[0], "r");
	
	tmp.write("%s\n" % '---------------------------------------------------------------')

	tmp.writelines(log);	
	
	tmp.write("%s\n" % '---------------------------------------------------------------')
		
	tmp.close()
	
	return 0

if __name__ == '__main__':	
	sys.exit(main(sys.argv[1:]))
