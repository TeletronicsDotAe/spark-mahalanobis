package org.apache.spark.mllib.linalg

import breeze.linalg.{Matrix => BM, Vector => BV}
import org.apache.spark.sql.functions.udf

object VectorUtils {
  def fromBreeze(breezeVector: BV[Double]): Vector = {
    Vectors.fromBreeze( breezeVector )
  }

  def asBreeze(vector: Vector): BV[Double] = {
    // this is vector.asBreeze in Spark 2.0
    vector.asBreeze
  }

  def asBreeze(m: Matrix): BM[Double] = {
    // this is vector.asBreeze in Spark 2.0
    m.asBreeze
  }

  val addVectors = udf {
    (v1: Vector, v2: Vector) => fromBreeze( asBreeze(v1) + asBreeze(v2) )
  }
  val subtractVectors = udf {
    (v1: Vector, v2: Vector) => fromBreeze( asBreeze(v1) - asBreeze(v2))
  }


}
