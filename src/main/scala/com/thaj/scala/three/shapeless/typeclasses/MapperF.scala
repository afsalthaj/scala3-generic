package com.thaj.scala.three.shapeless.typeclasses

trait MapperF[F[_], P, A] {
  type Out <: Tuple
  def apply(a: A): F[Out]
}

object MapperF {
  type Aux[F[_], P, A <: Tuple, B] = MapperF[F, P, A] {type Out =  B}
  def apply[F[_], P, A <: Tuple](using ev: MapperF[F, P, A]): MapperF.Aux[F, P, A, ev.Out] = ev

  given emptyMap[F[_], P](using M: Monad[F]): MapperF.Aux[F, P, EmptyTuple, EmptyTuple] = new MapperF[F, P, EmptyTuple] {
    type Out = EmptyTuple
    def apply(f: EmptyTuple): F[EmptyTuple] = M.pure(EmptyTuple)
  }

  given  nonEmptyTuple[F[_], A, P,  B, Tail1 <: Tuple](
    using C: Case.Aux[P, A, F[B]], 
    M: MapperF[F, P, Tail1]
  )(using MF: Monad[F]): MapperF.Aux[F, P, A *: Tail1, B *: M.Out] = new MapperF[F, P, A *: Tail1] {
      type Out = B *: M.Out
      def apply(a: A *: Tail1): F[Out] = MF.flatMap(C.apply(a.head))(bb => MF.map(M.apply(a.tail))(mOut => bb *: mOut))
   }

  extension[A <: Tuple](tuple: A) {
    // Users should be able to pass the right set of cases to mapper.
    // Say these cases are embedded in a type P. Now an instance of mapper
    // will be summoned given the instances of Case, which is going to be summoned
    // using the individual instances in type P. But then this mapper should be specific
    // to the phantom type P. Otherwise its an ambiguity.
    // In real shapeless, this P is of the type `Poly`
    def traverse[F[_]]: Traverse[F, A] = new Traverse[F, A](tuple)
  }

  class Traverse[F[_], A](tuple: A) {
    def apply[P](p: P)(using M: MapperF[F, P, A]): F[M.Out] = M.apply(tuple)
  }
}
