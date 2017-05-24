package com.tlt.percipio.outliers

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.FunSuite

class MahalanobisTest extends FunSuite {

  private val sc = initSpark()
  private val outlier = Array(100, 1.9)
  private val data: RDD[Array[Double]] = sc.makeRDD(Seq[Array[Double]](
    Array(1.1,1.9), Array(0.9,1.9), Array(1.1, 2.1), outlier,
    Array(0.9,2.1), Array(0.9,1.9), Array(1.1, 2.1), Array(1, 1.9)
  ))

  test("Distance") {
    val res: RDD[(Vector, Double)] = Mahalanobis.outliers(sc, data.map(Vectors.dense))

    assert(res.count() === 1)
    assert(res.collect().head._1.toArray === outlier)
  }

  private def initSpark() = {
    // turn of excessive logging
    Logger.getRootLogger().setLevel(Level.OFF)
    Logger.getLogger("org.apache.spark").setLevel(Level.OFF)

    val conf = new SparkConf().setAppName("PercipioLocationClustering").setMaster("local")
    new SparkContext(conf)
  }
}
