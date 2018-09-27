__author__ = 'Ryan Ahn'

from collections import OrderedDict
from os import listdir
from math import log
import operator

import ngram
import fileio


def tf_value(dictionary, word):
    ret_tf_value = dictionary[word] / float(len(dictionary))
    return ret_tf_value


def tf_analyze(path):
    text = fileio.read_file(path)
    dictionary = ngram.wordgram_analyze(str.lower(text))
    tf_dict = OrderedDict()

    for k, v in dictionary.items():
        tf_dict[str(k)] = tf_value(dictionary, str(k))
    sorted_tf_dict = OrderedDict(sorted(tf_dict.items(), key=operator.itemgetter(1), reverse=True))

    return sorted_tf_dict


def idf_value(dirpath, dict_map, word):
    fil_list = listdir(dirpath)
    word_count = 0

    for dict_elem in dict_map:
        if word in dict_elem:
            word_count += 1
    if word_count is 0:
        word_count = 1
    ret_value = log(len(fil_list)/word_count)
    return ret_value


def idf_analyze(dirpath, path):
    dict_map = ngram.wordgram_map(dirpath)
    contents = fileio.read_file(path)

    dict_file = ngram.wordgram_analyze(str.lower(contents))
    dict_idf = OrderedDict()
    for dict_elm in dict_file.keys():
        dict_idf[dict_elm] = idf_value(dirpath, dict_map, dict_elm)
    sorted_idf_dict = OrderedDict(sorted(dict_idf.items(), key=operator.itemgetter(1), reverse=True))

    return sorted_idf_dict


def tf_idf_analyze(dirpath, path):
    dict_tf = tf_analyze(path)
    dict_idf = idf_analyze(dirpath, path)

    tf_idf_dict = OrderedDict()
    for tf_elem in dict_tf.keys():
        tf_idf_dict[tf_elem] = dict_tf[tf_elem] * dict_idf[tf_elem]

    sorted_itf_idf_dict = OrderedDict(sorted(tf_idf_dict.items(), key=operator.itemgetter(1), reverse=True))

    return sorted_itf_idf_dict

