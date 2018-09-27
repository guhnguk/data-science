#!/usr/bin/env python3

import matplotlib.pyplot as plt
from matplotlib.image import imread


img = imread('/Volumes/Workspace/github/deep-learning-from-scratch/dataset/lena.png')

plt.imshow(img)
plt.show()
