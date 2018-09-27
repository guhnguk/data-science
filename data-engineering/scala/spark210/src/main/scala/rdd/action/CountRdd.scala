package rdd.action

import org.apache.spark.{SparkConf, SparkContext}

object CountRdd {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("CountRdd").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val rdd = sc.parallelize(1 to 10)
    val result = rdd.count()

    println(result)
  }
}