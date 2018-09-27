#!/usr/bin/evn python3
# -*- coding: utf8 -*-
from pyspark import SparkConf, SparkContext


def get_spark_context():
    conf = SparkConf().setAppName("mappatition-tranform").setMaster("local[*]")
    sc = SparkContext(conf=conf)
    
    return sc


def increase(numbers):
    print("DB Connected")

    return (i + 1 for i in numbers)


if __name__ == "__main__":
    # numbs = range(1, 11)
    # ret = increase(numbs)
    # for i in ret:
    #     print(i, end=",")

    sc = get_spark_context()
    rdd1 = sc.parallelize(range(1, 11), 4)
    rdd2 = rdd1.mapPartitions(increase)
    print(rdd2.collect())

