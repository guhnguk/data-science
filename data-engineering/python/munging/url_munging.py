#!/usr/bin/env python3
# -*- coding: utf-8 -*-


import urllib.request
import sys


try:
    with urllib.request.urlopen('http://www.networksciencelab.com') as doc:
        html = doc.read()
        print(html)
except:
    print('Could not open %s' % doc, file=sys.err)

print('---------------------------------------------')

import urllib.parse

URL = 'http://www.networksciencelab.com/index.html;param?foo=bar#content'
ret = urllib.parse.urlparse(URL)
print(ret)
print(ret[1])
