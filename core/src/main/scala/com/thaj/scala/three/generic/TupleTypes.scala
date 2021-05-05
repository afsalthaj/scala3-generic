package com.thaj.scala.three.generic

object TupleTypes {
  type TupleMap[T <: Tuple, F[_]] <: Tuple = T match {
    case EmptyTuple => EmptyTuple
    case h *: t => F[h] *: TupleMap[t, F]
  }

  type II = (Int, Int)
  val s: (Int, Int) = (1, 2)
  val s2: (Option[Int], Option[Int]) = (Some(1), Some(2))

  
  type TupleInverseMap[T, F[_]] <: Tuple = T match {
    case EmptyTuple => EmptyTuple
    case F[h] *: t => h *: TupleInverseMap[t, F]
  }

  type Head[T <: Tuple] = T match {
    case h *: t => h
  }

  type Second[T <: Tuple] = T match {
    case h *: h2 *: t => h2
  }

  type Tail[T <: Tuple] <: Tuple = T match {
    case h *: tail => tail
  }
}
