#!/usr/bin/env python3
# -*- coding: utf-8 -*-


import numpy as np


dirty = np.array([9, 4, 1, -0.01, -0.02, -0.001])
print(dirty)

whos_dirty = dirty < 0
print(whos_dirty)

dirty[whos_dirty] = 0
print(dirty)

print("----------------------------")

linear = np.arange(-1, 1.1, 0.2)
print(linear)
linear = (linear <= 0.5) & (linear >= -0.5)
print(linear)

print("----------------------------")
sap = np.array(["MMM", "ABT", "ABBV", "ACN", "ACE", "ATVI", "ADBE", "ADT"])
# sap = np.array(["1", "2", "3", "4", "5", "6", "7", "8"])
print(sap[[1, 2, -1]])