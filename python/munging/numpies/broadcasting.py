#!/usr/bin/env python3
# -*- coding: utf-8 -*-


import numpy as np

# 벡터 연산을 브로드캐스팅이라고 한다.

a = np.arange(4)
print("a=>", a)
print("a*5=>", a*5)
print("====================================")
b = np.arange(1, 5)
print("b=>", b)

c = a + b
print("c=>", c)

print("--------------------------------------")
eye = np.eye(4)
print(eye)
print("--------------------------------------")
ones = np.ones((4,)) * 0.01
print(ones)
print("--------------------------------------")
noise = eye + ones
print(noise)

print("--------------------------------------")
random = np.random.random([4, 4])
print(random)
random = random * 0.01
print(random)

noise = np.eye(4) + random
round = np.round(noise, 2)
print(round)
