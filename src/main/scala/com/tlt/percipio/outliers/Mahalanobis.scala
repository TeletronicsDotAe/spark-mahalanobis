package com.tlt.percipio.outliers

import breeze.linalg.inv
import org.apache.log4j.LogManager
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.linalg.{Matrix, Vector, VectorUtils, Vectors}
import org.apache.spark.rdd.RDD

/**
  * Implemented using breeze vectors/matrices as there are functions to
  * invert, multiply and transform vectors and matrices which are not available in the Spark Vector/RDD framework
  */
object Mahalanobis {
  private val log = LogManager.getLogger(Mahalanobis.getClass) // Spark uses Log4j, so do we

  /** Filter distances exceeding the standard deviation */
  def outliers(sc: SparkContext, data: RDD[Vector]): RDD[(Vector, Double)] = {
    val rm = new RowMatrix(data)
    val covariance: Matrix = rm.computeCovariance()
    val rddStats = rm.computeColumnSummaryStatistics()
    info(s"RDD max: ${rddStats.max}, min: ${rddStats.min}, mean: ${rddStats.mean}")

    // https://en.wikipedia.org/wiki/Mahalanobis_distance#Definition_and_properties
    val mean = VectorUtils.asBreeze(rddStats.mean)
    val sInv = inv(new breeze.linalg.DenseMatrix(covariance.numRows, covariance.numCols, covariance.toArray))

    log.debug("sInv: " + sInv)
    val res = data
      .map(VectorUtils.asBreeze)
      .map(v => {
        val diffV = v - mean
        val v2 = sInv * diffV
        val dSquared = diffV.t * v2
        val sqrt = Math.sqrt(dSquared)
        log.debug(s"diffV on $v is $diffV, v2: $v2, dSquared: $dSquared, sqrt: $sqrt")
        (VectorUtils.fromBreeze(v), sqrt)
      })

    val mahalanobisStats = new RowMatrix(res.map(_._2).map(Vectors.dense(_)))
      .computeColumnSummaryStatistics()

    val mahaMean = mahalanobisStats.mean.toArray.head
    val mahaVariance = mahalanobisStats.variance.toArray.head
    val mahaStandardDeviation = scala.math.sqrt(mahaVariance)
    info(s"Mahalanobis distance max: ${mahalanobisStats.max}, min: ${mahalanobisStats.min}, mean: ${mahaMean}, std deviation: ${mahaStandardDeviation}")

    res
      .filter(x => scala.math.abs(x._2 - mahaMean) > mahaStandardDeviation)
      .sortBy(-_._2)
  }

  private def info(msg: String) = log.info(msg)

}
