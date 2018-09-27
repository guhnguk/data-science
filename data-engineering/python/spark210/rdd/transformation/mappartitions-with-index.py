#!/usr/bin/env python3
# -*- coding: utf8 -*-

# filename: mappartitions-with-index

from pyspark import SparkConf, SparkContext


def get_spark_contenxt():
    conf = SparkConf().setAppName("mappartitions-with-index").setMaster("local[*]")
    sc = SparkContext(conf=conf)

    return sc


def increase_with_index(idx, numbers):
    for i in numbers:
        if (idx == 1):
            yield i

if __name__ == "__main__":
    sc = get_spark_contenxt()
    rdd1 = sc.parallelize(range(1, 11), 3)
    rdd2 = rdd1.mapPartitionsWithIndex(increase_with_index)
    print(rdd2.collect())