package com.thaj.scala.three.shapeless.typeclasses

/**
 * Naive map implementation of mapping a tuple.
 * 
 * tuple.mapElements, and uses the case in the implicit scope 
 * It infers the type properly (refer example) but the implicit scoping is terrible.
 * In real mapper we will use a phantom type to further narrow down the implicit scope
 * allowing user to pass the scope thats used
 */ 
trait MapperBasic[A <: Tuple] {
  type Out <: Tuple
  def apply(a: A): Out
}

object MapperBasic {
    type Aux[A <: Tuple, B] = MapperBasic[A] {type Out = B}
    def apply[A <: Tuple](using ev: MapperBasic[A]): MapperBasic.Aux[A, ev.Out] = ev

    given emptyMap: MapperBasic.Aux[EmptyTuple, EmptyTuple] = new MapperBasic[EmptyTuple] {
      type Out = EmptyTuple
      def apply(f: EmptyTuple): EmptyTuple = EmptyTuple
    }

    given  nonEmptyTuple[A, B, Tail1 <: Tuple](using C: Case.Aux[A, B], M: MapperBasic[Tail1]): MapperBasic.Aux[A *: Tail1, B *: M.Out] = new MapperBasic[A *: Tail1] {
      type Out = B *: M.Out
      def apply(a: A *: Tail1) = C.apply(a.head) *: M.apply(a.tail)
    }

    trait Case[A] {
      type Out
      def apply(a: A): Out
    }

    object Case {
      type Aux[A, B] = Case[A] { type Out = B }

      def createInstance[A, B](f: A => B): Case.Aux[A, B] = new Case[A]{
        type Out = B
        def apply(a: A) = f(a)
     }
   }

    extension[A <: Tuple](tuple: A) {
        // This is sort of a limited mapperBasic propogating the type information, but we haven't used the phantom type.
        def mapElements(using M: MapperBasic[A]): M.Out  = M.apply(tuple)
    }
}

