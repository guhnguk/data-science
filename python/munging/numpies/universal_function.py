#!/usr/bin/env python3
# -*- coding: utf-8 -*-


import numpy as np


stocks = np.array([140.49, 0.97, 40.68, 41.53, 55.7, 57.21, 98.2, 99.19, 109.96, 111.47, 35.71, 36.27, 87.85, 89.11, 30.22, 30.91])

print("stocks => ")
print(stocks)
print("stocks[0] => ")
print(stocks[0])
print("stocks[1] => ")
print(stocks[1])
print("---------------------------------")

changes = np.where(np.abs(stocks[1] - stocks[0]) > 1.00, stocks[1] - stocks[0], 0)
print("changes => ", changes)

print("---------------------------------")
stocks = stocks.reshape(8, 2).T
print(stocks)

print("---------------------------------")
fall = np.greater(stocks[0], stocks[1])
print(fall)

sap = np.array(["MMM", "ABT", "ABBV", "ACN", "ACE", "ATVI", "ADBE", "ADT"])
print(sap)
print(sap[fall])