package com.thaj.scala.three.generic.typeclasses

import com.thaj.scala.three.generic.Generic
import com.thaj.scala.three.generic.TupleTypes
import com.thaj.scala.three.generic.ops.tuple._

trait Second[A] {
  type Out
  def second(a: A): Option[Out]
}

object Second extends TupleSecondInstance with ProductSecondInstance {
  type Aux[A, B] = Second[A] {type Out = B}
  def apply[A](implicit ev: Second[A]): Aux[A, ev.Out] = ev
}

trait TupleSecondInstance {
  given headOfTuple[A <: Tuple]: Second.Aux[A, TupleTypes.Second[A]] = new Second[A] {
    override type Out = TupleTypes.Second[A]
    override def second(a: A): Option[TupleTypes.Second[A]] = secondOf(a)
  }
}

trait ProductSecondInstance {
  given secondInstanceOfProduct[A, Repr <: Tuple](using G: Generic.Aux[A, Repr]): Second.Aux[A, TupleTypes.Second[Repr]] = new Second[A] {
    override type Out = TupleTypes.Second[Repr]
    override def second(a: A): Option[Out] = {
      val tuple: Repr = G.to(a)
      secondOf(tuple)
    }
  }
}
