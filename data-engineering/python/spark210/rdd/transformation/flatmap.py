#!/usr/bin/env python3
# -*- coding: utf8 -*-


from pyspark import SparkConf, SparkContext


def get_spark_context():
    conf = SparkConf().setAppName("flatMapTransform").setMaster("local[*]")
    sc = SparkContext(conf=conf)

    return sc


if __name__ == "__main__":
    sc = get_spark_context()

    fruits = ["apple, orange", "grape, apple, mango", "blueberry, tomato, orange"]
    rdd1 = sc.parallelize(fruits)
    rdd2 = rdd1.flatMap(lambda s: s.split(", "))

    result = rdd2.collect()
    print(result)