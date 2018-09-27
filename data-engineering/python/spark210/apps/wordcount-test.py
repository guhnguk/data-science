#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from unittest import TestCase
from apps.wordcount import WordCount


class TestWordCount(TestCase):
    def test_wordcount(self):
        wc = WordCount()
        sc = wc.get_spark_context("WordCountTest", "local[*]")
        input = ["Apache Spark is a fast and general engine for large-scale data processing.",
                 "Spark runs on both Windows and UNIX-like  systems"]
        input_rdd = sc.parallelize(input)
        result_rdd = wc.process(input_rdd)
        result_map = result_rdd.collectAsMap()

        self.assertEqual(result_map['Spark'], 2)
        self.assertEqual(result_map['UNIX-like'], 1)
        self.assertEqual(result_map['runs'], 1)

        print(result_map)

        sc.stop()