__author__ = 'Ryan Ahn'

from collections import OrderedDict
from os import listdir, path

import re

import fileio
import ngram


def ngram_analysis(n, contents):
    ngram_dictionary = OrderedDict()

    for i in range(0, len(contents) - n):
        word = ""

        for j in range(0, n):
            if contents[i + j] != "\n":
                word += contents[i + j]

        if word in ngram_dictionary:
            ngram_dictionary[word] += 1
        else:
            ngram_dictionary[word] = 1

    return ngram_dictionary


def wordgram_analyze(contents):
    wordgram_dictionary = OrderedDict()

    contents_len = len(contents)
    print (contents_len)

    i = 0
    while i < contents_len:
        word = ""
        jmp_count = 1

        sub_contents_len = contents_len - i

        for j in range(0, sub_contents_len):
            print (contents[i + j])
            if contents[i + j] == " " or contents[i + j] == "\r" or contents[i + j] == "\n":
                jmp_count = j + 1
                # if j == 0:
                #     jmp_count = j + 2
                # else:
                #     jmp_count = j + 1
                break
            else:
                word += contents[i + j]
        if len(word) < 1:
            i += jmp_count
            continue
        if word in wordgram_dictionary:
            print (word)
            wordgram_dictionary[word] += 1
        else:
            print (word)
            wordgram_dictionary[word] = 1

        if (i + j + 1) == contents_len:
            break

        i += jmp_count

    return wordgram_dictionary


def wordgram_map(dirpath):
    file_list = listdir(dirpath)
    dict_map = []

    for f in file_list:
        extension = path.splitext(f)[1]
        if extension != ".txt":
            continue
        full_path = dirpath + f
        file_contents = fileio.read_file(full_path)
        file_dict = ngram.wordgram_analyze(str.lower(file_contents))
        dict_map.append(file_dict)

    return dict_map


if __name__ == "__main__":
    text = "China has a strong economy that is growing at a rapid pace. However politically it differs greatly from the US Economy.\n"
    # text = "China has a strong economy that is growing at a rapid pace. However politically it "

    text = re.sub('[^0-9a-zA-Z\\s]', '', text)
    # text = re.sub('[^0-9a-zA-Z]', '', text)

    print (text)
    print (len(text))
    result = wordgram_analyze(text)

    print (result)
