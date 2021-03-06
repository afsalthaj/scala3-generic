package com.thaj.scala.three.generic.typeclasses

import com.thaj.scala.three.generic.TupleTypes._
import com.thaj.scala.three.generic.ops.tuple._

object InbuiltSimpleMapExample extends App {
  val tuple: (Int, Double) = 
    1 *: 1.0 *: EmptyTuple

  // Type lambda - already available in Scala3, which is different to def toOption[A](a: A): Option[A]
  // [A] => (a: A) => Option[A]
  val s: (Option[Int], Option[Double]) = 
    tuple.mapInferred([A] => (a: A) => Some(a))

  println(s)
}