package com.thaj.scala.three

import scala.Tuple.InverseMap

object TupleAnoHListAno:
  def processTuple(tup1: (Int, String, Double)): Option[Any] =
    tup1 match {
      case h *: t => Some(h)
      case EmptyTuple => None
    }
  
  val x: Int *: (String, Boolean) = (1, "niceanu", true)

  // Map on tuple: Type computation.
  type TupleF[F[_]] = (F[String], F[Int], F[Boolean])
  
  // Or may be we could do - limited by number of terms
  type TupleF2[F[_], A, B, C] = (F[A], F[B], F[C])
  
  // How about no limits in number of terms
  // and still define a type that says all 
  // my terms are wrapped in an F ?
  // Sort of computing the types - 
  type TupleMap[T <: Tuple, F[_]] <: Tuple = T match {
      case EmptyTuple => EmptyTuple 
      case h *: t => F[h] *: TupleMap[t, F]
    }

  // this is going to compute at compile time!
  val y: TupleMap[(Int, String, Boolean), Option] = 
    (Some(1), Some("bhoom"), Some(true))
  
  type TupleInverseMap[T, F[_]] = T match {
    case EmptyTuple => EmptyTuple
    case F[h] *: t => h *: TupleInverseMap[t, F]
  }

  type Last[T <: Tuple]  = T match {
    case h *: EmptyTuple => h
    case h *: t => Last[t]
    case _ => Last[T]
  }