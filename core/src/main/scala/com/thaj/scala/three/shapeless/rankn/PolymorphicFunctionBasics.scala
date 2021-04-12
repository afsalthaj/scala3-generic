package com.thaj.scala.three.shapeless.rankn

// https://blog.oyanglul.us/scala/dotty/en/rank-n-type
object rankn {
  // Fails to compile
  // def rank[A, C](a: (Int, String), id: A => A): (Int, String) = 
     // (id(a._1), id(a._2))

  // A scala2 solution
  trait IdentityFn {
    def apply[A](value: A): A = value
  }

  def rank[A](a: (Int, String), identityFn: IdentityFn): (Int, String) = 
    (identityFn(a._1), identityFn(a._2))

  // A scala3 solution
  def rank[A, B, C](a: (B, C), id: [A] => A => A): (B, C) = 
    (id(a._1), id(a._2))
}
