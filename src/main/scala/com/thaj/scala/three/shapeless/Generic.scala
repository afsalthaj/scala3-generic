package com.thaj.scala.three.shapeless

import TupleTypes.{Head, Second}
import scala.deriving._

trait Generic[A] {
  type Repr
  def to(a: A): Repr
  // Later addition
  def from(a: Repr): A
}

object Generic {
  type Aux[A, B] = Generic[A] {type Repr = B}

  def apply[A](implicit ev: Generic[A]): Aux[A, ev.Repr] = ev

  // given genericOfTuple[H, Tail <: Tuple, A <: H *: Tail]: Generic.Aux[A, H *: Tail] = new Generic[A] {
  //   override type Repr = H *: Tail
  //   override def to(a: A): H *: Tail = a
  //   override def from(a: H *: Tail): A = a.head *: a.tail
  // }

  given genericOfA[A <: Product](using P: Mirror.ProductOf[A]): Generic.Aux[A, P.MirroredElemTypes] = new Generic[A] {
    override type Repr =  P.MirroredElemTypes
    override def to(a: A): P.MirroredElemTypes = Tuple.fromProductTyped(a)
    override def from(a: P.MirroredElemTypes) = P.fromProduct(a)
  }
}
import com.thaj.scala.three.macros.Macros
