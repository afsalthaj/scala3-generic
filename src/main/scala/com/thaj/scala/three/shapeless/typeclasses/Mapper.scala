package com.thaj.scala.three.shapeless.typeclasses

trait Mapper[A] {
  type Out
  def map(f: A => Out): Out
}

object Mapper {
    type Aux[A, B] = Mapper[A] {type Out = B}
    def apply[A](using ev: Mapper[A]): Mapper.Aux[A, ev.Out] = ev
}