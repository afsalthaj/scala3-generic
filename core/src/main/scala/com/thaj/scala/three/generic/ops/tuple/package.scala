package com.thaj.scala.three.generic.ops

import com.thaj.scala.three.generic.TupleTypes.{Head, Second, TupleInverseMap, TupleMap, Tail}
import com.thaj.scala.three.generic.Generic
import com.thaj.scala.three.generic.typeclasses
import com.thaj.scala.three.generic.typeclasses.Case
import com.thaj.scala.three.generic.typeclasses.Monad

package object tuple {
  import Generic._

  def unsafeSequence[T <: Tuple](x: T): Option[TupleInverseMap[T, Option]] = {
    val tupleUnwrapped: Array[Any] = x.productIterator.collect { case Some(v) => v }.toArray
    if x.productArity == tupleUnwrapped.length then Some(Tuple.fromArray(tupleUnwrapped).asInstanceOf[TupleInverseMap[T, Option]]) else None
  }


  //  def sequence[A, T <: Tuple, F[_]](t: TupleMap[T, F])(using MF: Monad[F]): F[TupleInverseMap[A, F]] = 
  //     t.traverse[F].apply[identityTupleF.type, T](identityTupleF)

  def headOf[T <: Tuple](c: T): Head[T] = {
    c.productIterator.toList.head.asInstanceOf[Head[T]]
  }

  def secondOf[T <: Tuple](a: T): Option[Second[T]] = { 
    a.productIterator.toList.lift(1).map(_.asInstanceOf[Second[T]])
  }

  def tailOf[T <: Tuple](a: T): Tail[T] = a match {
    case h *: tail => tail.asInstanceOf[Tail[T]]
  }

  // Stupid identity that inspects just the head and form a tuple back  - for demonstation
  def identity[A, Repr <: Tuple, H1, T <: Tuple](a: A)(
    using AG: Generic.Aux[A, Repr],
    h: typeclasses.Head.Aux[Repr, H1],
    t: typeclasses.Tail.Aux[Repr, T],
  ): H1 *: T = {
    val repr: Repr = AG.to(a)
    val tail: T = t.tail(repr)
    val head: H1 = h.head(repr)
    head *: tail
  }

  // Type inference issues.  val s: (Option[Int], Option[Double]) = ??? will not work
  def mapNonInferred[A <: Tuple, F[_]](a: A, f: [A] => A => F[A]): TupleMap[A, F] = 
    Tuple.fromArray(a.productIterator.map(a => f(a)).toArray).asInstanceOf[TupleMap[A, F]]

  extension[A <: Tuple](a: A) {
    def mapInferred[F[_]](f: [A] => A => F[A]): TupleMap[A, F] = 
      mapNonInferred[A, F](a, f)
  }
}
