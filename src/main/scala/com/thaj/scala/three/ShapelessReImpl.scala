package com.thaj.scala.three

import scala.deriving._
import com.thaj.scala.three.TypeComputation._

object ShapelessReImpl {

  trait Generic[A] {
    type Repr
    def to(a: A): Repr
  }

  object Generic {
    type Aux[A, B] = Generic[A] {type Repr = B}

    def apply[A](implicit ev: Generic[A]): Aux[A, ev.Repr] = ev
    
     given genericOfA[A <: Product](using P: Mirror.ProductOf[A]): Generic.Aux[A, P.MirroredElemTypes] = new Generic[A] {
      override type Repr =  P.MirroredElemTypes
      override def to(a: A): P.MirroredElemTypes = Tuple.fromProductTyped(a)
    }
    
     def headOfProduct[A <: Product](using P: Mirror.ProductOf[A])(a: A): Option[Head[P.MirroredElemTypes]] = {
      val tuple = Generic[A].to(a)
      TupleOps.headOf(tuple)
    }

    def secondOfProduct[A <: Product](using P: Mirror.ProductOf[A])(a: A): Option[Second[P.MirroredElemTypes]] = {
      val tuple = Generic[A].to(a)
      TupleOps.secondOf(tuple)
    }
  }
}
