#!/usr/bin/env python3
# -*- coding: utf8 -*-

# refs: https://xmlangel.github.io/python-download/

import urllib.request

# URL 저장 경로 지정하기
url = "https://hub.coursera-notebooks.org/user/suxusznaidlhvdhzetvziz/tree/week4/Face%20Recognition/weights"

file_name = ".csv"
full_url = url + "/" + file_name

file_path = "/Users/ryan/Downloads"
full_path = file_path + "/" + file_name

urllib.request.urlretrieve(full_url, full_path)
