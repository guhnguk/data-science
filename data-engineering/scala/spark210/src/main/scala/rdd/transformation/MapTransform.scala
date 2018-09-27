package rdd.transformation

import org.apache.spark.{SparkConf, SparkContext}

object MapTransform {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("MapTranform").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val rdd1 = sc.parallelize(1 to 5)
    val rdd2 = rdd1.map(_ + 1)

    println(rdd2.collect().mkString(", "))
  }
}
