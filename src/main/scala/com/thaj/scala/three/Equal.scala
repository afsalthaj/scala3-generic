package com.thaj.scala.three

import com.thaj.scala.three.ShapelessReImpl.Generic

trait Equal[A] {
  def eq(a: A, b: A): Boolean
}

object Equal {
  def apply[A](using ev: Equal[A]): Equal[A] = ev
  
  given Equal[String] with  {
    override def eq(a: String, b: String): Boolean = true
  }
  
  given Equal[Int] with {
    override def eq(a: Int, b: Int): Boolean = true
  }
  
  given Equal[Double] with {
    override def eq(a: Double, b: Double): Boolean = true
  }
  
  given Equal[EmptyTuple] with {
    override def eq(a: EmptyTuple, b: EmptyTuple): Boolean =
      true
  }
  
  given eqInstanceOfTuple[A, T <: Tuple](using A1: Equal[A], A2: Equal[T]): Equal[A *: T] = new Equal[A *: T] {
    override def eq(a: A *: T, b: A *: T): Boolean = (a, b) match {
      case (h *: t, h1 *: t1) =>
        Equal[A].eq(h, h1) && Equal[T].eq(t, t1)
    }
  }
  
  given eqInstanceOfProduct[A, Repr <: Tuple](using G: Generic.Aux[A, Repr], ReprEq: Equal[Repr]): Equal[A] = new Equal[A] {
    override def eq(a: A, b: A): Boolean =
      ReprEq.eq(G.to(a), G.to(b))
  }
}