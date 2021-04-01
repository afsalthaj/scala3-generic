package com.thaj.scala.three.shapeless

import TupleTypes.{Head, Second}
import scala.deriving._

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
}