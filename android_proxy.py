#!/usr/bin/env python

import sys
import os

def main(argv):
	if len(argv) > 2:
		os.system('adb shell am broadcast -a org.kitdroid.action.UPDATE_PROXY --es host "%s" --ei port %s' % (argv[1], argv[2]))
	else:
		os.system('adb shell am broadcast -a org.kitdroid.action.UPDATE_PROXY')
if __name__ == '__main__':
    main(sys.argv)