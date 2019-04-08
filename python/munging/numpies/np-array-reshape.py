#!/usr/bin/env python3
# -*- coding: utf-8 -*-


import numpy as np

# sap = np.array(["MMM", "ABT", "ABBV", "ACN", "ACE", "ATVI", "ADBE", "ADT"])
sap = np.array(["1", "2", "3", "4", "5", "6", "7", "8"])
print(sap)
print(sap.dtype)

print("--------------------------------")
sap2d = sap.reshape(2, 4)
print("sap2d=>")
print(sap2d)
sap2d = sap2d.T
print(sap2d)

print("--------------------------------")
sap3d = sap.reshape(2, 2, 2)
print(sap3d)
sap3d = sap3d.swapaxes(1, 2)
print(sap3d)

print("--------------------------------")
sap3d = sap3d.transpose((0, 2, 1))
print(sap3d)