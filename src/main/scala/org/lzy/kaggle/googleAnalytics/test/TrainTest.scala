package org.lzy.kaggle.googleAnalytics.test

import java.text.SimpleDateFormat

import common.SparkUtil
import org.lzy.kaggle.googleAnalytics.Constants

import scala.util.Try

object TrainTest {
  val sdf = new SimpleDateFormat("yyyyMMdd")

  def main(args: Array[String]): Unit = {
    run2()
    run()
  }
  def run()={
    val date="20170503"
      println(Try(sdf.parse(date.trim).getTime).toOption)
//    -7362962158
//     1481904000
  }

  def run2()={
    val spark=SparkUtil.getSpark()
    val sc=spark.sparkContext

    sc.textFile(Constants.trainPath)
      .map(_.split(",")(1))
      //      .filter(date=>date.startsWith("201"))
      .distinct().map(date=>{
      val l=Try(sdf.parse(date)).toOption
      (date,l)
    }
    ).foreach(println)
//      .distinct().collect().sorted.foreach(println)
  }
}
