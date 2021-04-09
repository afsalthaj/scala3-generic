package com.thaj.scala.three.shapeless.typeclasses

import com.thaj.scala.three.shapeless.Generic
import com.thaj.scala.three.shapeless.TupleTypes
import com.thaj.scala.three.shapeless.ops.tuple._

trait Head[A] {
  type Out
  def head(a: A): Out
}

object Head extends TupleHeadInstances with ProductHeadInstances {
  type Aux[A, B] = Head[A] {type Out = B}
  def apply[A](implicit ev: Head[A]): Aux[A, ev.Out] = ev
}

trait TupleHeadInstances {
  given headOfTuple[A <: Tuple]: Head.Aux[A, TupleTypes.Head[A]] = new Head[A] {
    override type Out = TupleTypes.Head[A]
    override def head(a: A): TupleTypes.Head[A] = headOf(a)
  }
}

trait ProductHeadInstances {
  given headInstanceOfProduct[A, Repr <: Tuple](using G: Generic.Aux[A, Repr]): Head.Aux[A, TupleTypes.Head[Repr]] = new Head[A] {
    override type Out = TupleTypes.Head[Repr]
    override def head(a: A): TupleTypes.Head[Repr] = {
      val tuple: Repr = G.to(a)
      headOf(tuple)
    }
  }
}