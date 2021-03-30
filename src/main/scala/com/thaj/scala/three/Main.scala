package com.thaj.scala.three

import TupleAnoHListAno._
import scala.deriving._


object Main:
  case class Hello(a: String, b: String)
  
  def main(args: Array[String]) =
    val z: Option[(Int, String)] = unsafeSequence((Some(1), Some("afsal")))
    println(z)
    
    val y: (String, String) = ("afsal", "thaj")
    val result: String = lastOf(y)
    println(result)

  def unsafeSequence[T <: Tuple](x: T): Option[TupleInverseMap[T, Option]] = {
    val tupleUnwrapped: Array[Any] = x.productIterator.collect { case Some(v) => v }.toArray
    if x.productArity == tupleUnwrapped.length then Some(Tuple.fromArray(tupleUnwrapped).asInstanceOf[TupleInverseMap[T, Option]]) else None
  }
  
  def lastOf[T <: Tuple](c: T): Last[T] = {
    c.productIterator.toList.last.asInstanceOf[Last[T]]
  }
    
