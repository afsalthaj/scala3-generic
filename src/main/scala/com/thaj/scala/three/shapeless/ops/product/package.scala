package com.thaj.scala.three.shapeless.ops

import com.thaj.scala.three.shapeless.TupleTypes.{Head, Second}
import com.thaj.scala.three.shapeless.Generic
import com.thaj.scala.three.shapeless.ops.tuple._
import scala.deriving._

package object product {
  def headOfProduct[A <: Product](using P: Mirror.ProductOf[A])(a: A): Option[Head[P.MirroredElemTypes]] = {
    val tuple = Generic[A].to(a)
    headOf(tuple)
  }

  def secondOfProduct[A <: Product](using P: Mirror.ProductOf[A])(a: A): Option[Second[P.MirroredElemTypes]] = {
    val tuple = Generic[A].to(a)
    secondOf(tuple)
  }
}
