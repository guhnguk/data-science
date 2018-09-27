#!/usr/bin/env python3
# -*- coding: utf8 -*-


from pyspark import SparkConf, SparkContext


def get_spark_context():
    conf = SparkConf().setAppName("map-transform").setMaster("local[*]")
    sc = SparkContext(conf=conf)
    sc.setLogLevel("WARN")

    return sc


if __name__ == "__main__":
    sc = get_spark_context()
    rdd1 = sc.parallelize(range(1, 6))

    rdd2 = rdd1.map(lambda x: x + 1)
    result = rdd2.collect()

    print(result)