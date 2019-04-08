#!/usr/bin/env python3


import numpy as np


x = np.array([1.0, 2.0, 3.0])
print(x)

print(type(x))

x = np.array([1.0, 2.0, 3.0])
y = np.array([2.0, 4.0, 6.0])
print(x)
print(y)
print("----------------")
print("x + y")
print(x + y)

print("----------------")
print("x - y")
print(x - y)

print("----------------")
print("x * y")
print(x * y)

print("----------------")
print("x / y")
print(x / y)

print("----------------")
x = np.array([1.0, 2.0, 3.0])
print(x/2.0)

print("----------------")
A = np.array([[1, 2], [3, 4]])
print(A)

print(A.shape)
print(A.dtype)

B = np.array([[3, 0], [0, 6]])
print(B)
print(A + B)

print("----------------")
print(A * B)
print(A * 10)

print("----------------")
A = np.array([[1, 2], [3, 4]])
B = np.array([10, 20])
print(A * B)

print("----------------")
X = np.array([[51, 55], [14, 19], [0, 4]])
print(X)

print(X[0])
print(X[0][1])

for row in X:
    print("=>")
    print(row)

X = X.flatten()
print(X)
print(X[np.array([0, 2, 4])])

print(X > 15)
print(X[X > 15])