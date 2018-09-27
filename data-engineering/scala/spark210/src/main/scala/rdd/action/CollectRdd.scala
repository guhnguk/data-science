package rdd.action

import org.apache.spark.{SparkConf, SparkContext}

object CollectRdd {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("collectRdd").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val rdd = sc.parallelize(1 to 10)
    val result = rdd.collect
    println(result.mkString(", "))
  }
}
