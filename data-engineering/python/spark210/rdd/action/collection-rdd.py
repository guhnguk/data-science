#!/usr/bin/env python3
# -*- coding: utf8 -*-
from pyspark import SparkConf, SparkContext


def get_spark_context():
    conf = SparkConf().setAppName("collecRdd").setMaster("local[*]")
    sc = SparkContext(conf=conf)

    return sc


if __name__ == "__main__":
    sc = get_spark_context()

    rdd = sc.parallelize(range(1, 11))
    result = rdd.collect()

    print(result)