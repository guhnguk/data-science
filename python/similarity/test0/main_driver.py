__author__ = 'Ryan Ahn'
#-*- coding: utf-8 -*-

from collections import OrderedDict
import re


def doc_tokenized(doc):
    # doc = re.sub('[^0-9a-zA-Z\\s]', '', doc)
    doc = str.lower(doc)
    doc_length = len(doc)

    wordcount_dic = OrderedDict()

    i = 0
    while i < doc_length:
        word = ""
        jmp_cnt = 1

        for j in xrange(0, doc_length - i):
            if doc[i + j] == " " or doc[i + j] == "\n" or doc[i + j] == "\n":
                jmp_cnt = j + 1
                break
            else:
                word += doc[i + j]

        if len(word) < 1:
            i += jmp_cnt
            continue

        if word in wordcount_dic:
            print(word)
            wordcount_dic[word] += 1
        else:
            print(word)
            wordcount_dic[word] = 1

        if (i + j + 1) == doc_length:
            break

        i += jmp_cnt

    return wordcount_dic


def tf_analyze(docu_list):
    toknized_dictionary = OrderedDict()
    doc_index = 0
    for doc in docu_list:
        toknized_dictionary[doc_index] = doc_tokenized(doc)
        doc_index += 1

    return toknized_dictionary


if __name__ == "__main__":
    doc1 = ""


    # doc1 = " China has a strong economy that is growing at a rapid pace. However politically it differs greatly from the US Economy."
    # doc2 = " At last, China seems serious about confronting an endemic problem: domestic violence and corruption."
    # doc3 = " Japan's prime minister, Shinzo Abe, is working towards healing the economic turmoil in his own country for his view on the future of his people."
    # doc4 = " Vladimir Putin is working hard to fix the economy in Russia as the Ruble has tumbled."
    # doc5 = " What's the future of Abenomics? We asked Shinzo Abe for his views."
    # doc6 = " Obama has eased sanctions on Cuba while accelerating those against the Russian Economy, even as the Ruble's value falls almost daily."
    # doc7 = " Vladimir Putin was found to be riding a horse, again, without a shirt on while hunting deer. Vladimir Putin always seems so serious about things - even riding horses."

    result = doc_tokenized(doc1)
    print(result)

    # doc_list = [doc1, doc2, doc3, doc4, doc5, doc6, doc7]
    # result = tf_analyze(doc_list)
