package com.thaj.scala.three.shapeless.typeclasses

trait Monad[F[_]] {
  def pure[A](a: A): F[A]
  def map[A, B](f: F[A])(g: A => B): F[B]
  def flatMap[A, B](f: F[A])(g: A => F[B]): F[B]
}

object Monad {
  given Monad[Option] with {
    def pure[A](a: A): Option[A] = Some(a)
    
    def map[A, B](f: Option[A])(g: A => B): Option[B] = 
      f.map(g)

    def flatMap[A, B](f: Option[A])(g: A => Option[B]): Option[B] = 
      f.flatMap(g)
   } 
}