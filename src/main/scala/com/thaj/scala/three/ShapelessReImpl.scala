package com.thaj.scala.three

import scala.deriving._
import com.thaj.scala.three.TypeComputation._

object ShapelessReImpl {

  trait Generic[A, B] {
    def to(a: A): B
  }

  object Generic {
    def apply[A, B](using M: Generic[A, B]): Generic[A, B] = M

     given genericOfA[A <: Product] (using P: Mirror.ProductOf[A]): Generic[A, P.MirroredElemTypes] = new Generic[A, P.MirroredElemTypes] {
      override def to(a: A): P.MirroredElemTypes = Tuple.fromProductTyped(a)
    }
    
     def headOfProduct[A <: Product](using P: Mirror.ProductOf[A])(a: A): Option[Head[P.MirroredElemTypes]] = {
      val tuple = Generic[A, P.MirroredElemTypes].to(a)
      TupleOps.headOf(tuple)
    }

    def secondOfProduct[A <: Product](using P: Mirror.ProductOf[A])(a: A): Option[Second[P.MirroredElemTypes]] = {
      val tuple = Generic[A, P.MirroredElemTypes].to(a)
      TupleOps.secondOf(tuple)
    }
  }

}
