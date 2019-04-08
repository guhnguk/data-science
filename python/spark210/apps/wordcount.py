#!/usr/bin/env python3
# -*- coding: utf8 -*-


import sys
from operator import add
from pyspark import SparkConf, SparkContext


class WordCount:
    def get_spark_context(self, app_name, master):
        conf = SparkConf().setAppName(app_name).setMaster(master)

        return SparkContext(conf=conf)

    def get_input_rdd(self, sc, input):
        return sc.textFile(input)

    def process(self, input_rdd):
        words = input_rdd.flatMap(lambda s : s.split(" "))
        wc_pair = words.map(lambda s: (s, 1))
        return wc_pair.reduceByKey(add)


if __name__ == "__main__":
    wc = WordCount()
    sc = wc.get_spark_context("WordCount", sys.argv[1])

    sc.setLogLevel("ERROR")

    input_rdd = wc.get_input_rdd(sc, sys.argv[2])
    result_rdd = wc.process(input_rdd)

    result_rdd.saveAsTextFile(sys.argv[3])
    sc.stop()