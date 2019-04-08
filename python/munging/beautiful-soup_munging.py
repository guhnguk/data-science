#!/usr/bin/env python3
# -*- coding: utf-8 -*-


from bs4 import BeautifulSoup
from urllib.request import urlopen

# soup1 = BeautifulSoup("<HTML><HEAD><header></HEAD></HTML>")
# soup2 = BeautifulSoup(open("myDoc.html"))
# soup3 = BeautifulSoup(urlopen("http://www.networksciencelab.com/"))


html_string = '''
        <HTML>
            <HEAD><TITLE>My document<TITLE></HEAD>
            <BODY>Main text.</BODY>
        </HTML>
'''


soup = BeautifulSoup(html_string)
ret = soup.get_text()

print(ret)

print('-----------------------------------------')

with urlopen("http://www.networksciencelab.com/") as doc:
    soup = BeautifulSoup(doc)


