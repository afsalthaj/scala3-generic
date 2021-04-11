package com.thaj.scala.three.shapeless.typeclasses

import com.thaj.scala.three.shapeless.TupleTypes
import com.thaj.scala.three.shapeless.ops.tuple._


trait Tail[A] {
   type Out
   def tail(a: A): Out 
}

object Tail {
  type Aux[A, B] = Tail[A]{type Out = B}
  
  def apply[A](using ev: Tail[A]): Aux[A, ev.Out] = ev

  given tailOfTuple[A <: Tuple]: Tail.Aux[A, TupleTypes.Tail[A]] = new Tail[A] {
    type Out = TupleTypes.Tail[A]
    def tail(a: A): TupleTypes.Tail[A] = tailOf(a)
  }
}