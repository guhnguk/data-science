#!/usr/bin/env python3
# -*- coding: utf8 -*-

# filename: mapvalue.py
from pyspark import SparkConf, SparkContext


def get_spark_context():
    conf = SparkConf().setAppName("mapvalue").setMaster("local[*]")
    sc = SparkContext(conf=conf)

    return sc


if __name__ == "__main__":
    sc = get_spark_context()
    rdd1 = sc.parallelize(["a", "b", "c"], 3)
    rdd2 = rdd1.map(lambda v: (v, 1))
    rdd3 = rdd2.mapValues(lambda i: i + 1)
    print(rdd3.collect())