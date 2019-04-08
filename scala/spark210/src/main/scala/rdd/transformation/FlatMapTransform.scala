package rdd.transformation

import org.apache.spark.{SparkConf, SparkContext}

object FlatMapTransform {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
                        .setAppName("FlatMapTransform")
                        .setMaster("local[*]")

    val sc = new SparkContext(conf)

    val fruits = List("apple, orange"
                    , "grape, apple, mango"
                    , "blueberry, tomato, orange")
    val rdd1 = sc.parallelize(fruits)

    val rdd2 = rdd1.map(_.split(", "))
    println(rdd2.collect().map(_.mkString("{", ", ", "}")))
    println(rdd2.collect().map(_.mkString("{", ", ", "}")).mkString("{", ", ", "}"))

    val rdd3 = rdd1.flatMap(_.split(", "))
    println(rdd3.collect().mkString("{", ",", "}"))
  }
}
