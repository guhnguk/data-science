#!/usr/bin/env python3
# -*- coding: utf-8 -*-


import numpy as np

numbers = np.array(range(1, 11), copy=True)
print(numbers)
print("------------------------------------------------")

ones = np.ones([2, 4], dtype=np.float64)
print(ones)
print("------------------------------------------------")

zeros = np.zeros([2, 4], dtype=np.float64)
print(zeros)
print("------------------------------------------------")

empty = np.empty([2, 4], dtype=np.float64)
print(empty)

print("------------------------------------------------")
print(ones.shape)
print(numbers.ndim)
print(zeros.dtype)

print("------------------------------------------------")
eye = np.eye(3, k=1)
print(eye)

print("------------------------------------------------")
sap = np.array(["MMM", "ABT", "ABBV", "ACN", "ACE", "ATVI", "ADBE", "ADT"])
print(sap)
print(sap.dtype)

sap2d = sap.reshape(2, 4)
print(sap2d)

print("------------------------------------------------")
sap3d = sap.reshape(2, 2, 2)
print(sap3d)

