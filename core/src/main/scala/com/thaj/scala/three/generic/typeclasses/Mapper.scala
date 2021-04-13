package com.thaj.scala.three.generic.typeclasses

import com.thaj.scala.three.generic.TupleTypes.{TupleInverseMap, TupleMap}
/**
 * A better implementation of MapperBasic, where
 * now users have control over the list of cases that can be used to map over the tuple.
 */ 
trait Mapper[P, A <: Tuple] {
  type Out <: Tuple
  def apply(a: A): Out
}

object Mapper {
    type Aux[P, A <: Tuple, B] = Mapper[P, A] {type Out = B}
    def apply[P, A <: Tuple](using ev: Mapper[P, A]): Mapper.Aux[P, A, ev.Out] = ev

    given emptyMap[P]: Mapper.Aux[P, EmptyTuple, EmptyTuple] = new Mapper[P, EmptyTuple] {
      type Out = EmptyTuple
      def apply(f: EmptyTuple): EmptyTuple = EmptyTuple
    }

    // The fun element here, the instances of case is going to derived from the companion objects of either `P`, `A` or `B`. 
    // Ok, that would mean we group the required cases in the companion object of a phantom type.
    given  nonEmptyTuple[A, P,  B, Tail1 <: Tuple](using C: Case.Aux[P, A, B], M: Mapper[P, Tail1]): Mapper.Aux[P, A *: Tail1, B *: M.Out] = new Mapper[P, A *: Tail1] {
      type Out = B *: M.Out
      def apply(a: A *: Tail1) = C.apply(a.head) *: M.apply(a.tail)
    }

    extension[A <: Tuple](tuple: A) {
        // Users should be able to pass the right set of cases to mapper.
        // Say these cases are embedded in a type P. Now an instance of mapper
        // will be summoned given the instances of Case, which is going to be summoned
        // using the individual instances in type P. But then this mapper should be specific
        // to the phantom type P. Otherwise its an ambiguity.
        // In real shapeless, this P is of the type `Poly`
        def mapElements[P](p: P)(using M: Mapper[P, A]): M.Out  = M.apply(tuple)
    }
}

trait Case[P, A] {
  type Out
  def apply(a: A): Out
}

object Case {
  type Aux[P, A, B] = Case[P, A] { type Out = B }

  def createInstance[P, A, B](f: A => B): Case.Aux[P, A, B] = new Case[P, A]{
    type Out = B
    def apply(a: A) = f(a)
  }
}
