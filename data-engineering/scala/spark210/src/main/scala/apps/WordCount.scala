package apps

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
  def getSparkContext(appName: String, master: String) = {
    val conf = new SparkConf().setAppName(appName).setMaster(master)
    new SparkContext(conf)
  }

  def getInputRDD(sc: SparkContext, input: String) = {
    sc.textFile(input)
  }

  def process(inputRDD: RDD[String]) = {
    val words = inputRDD.flatMap(str => str.split(" "))
    val wcPair = words.map((_, 1))

    wcPair.reduceByKey(_ + _)
  }

  def handleResult(resultRDD: RDD[(String, Int)], output: String): Unit = {
    resultRDD.saveAsTextFile(output);
  }

  def main(args: Array[String]): Unit = {
    require(args.length == 3, "Usage: WordCount <Master> <Input> <Output>")

    val sc = getSparkContext("WordCount", args(0))
    val inputRDD = getInputRDD(sc, args(1))
    val resultRDD = process(inputRDD)

    handleResult(resultRDD, args(2))
  }
}
