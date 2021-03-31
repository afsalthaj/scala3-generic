package com.thaj.scala.three

import TypeComputation._
import com.thaj.scala.three.ShapelessReImpl.Generic

import scala.deriving._

object TupleOps {
  def unsafeSequence[T <: Tuple](x: T): Option[TupleInverseMap[T, Option]] = {
    val tupleUnwrapped: Array[Any] = x.productIterator.collect { case Some(v) => v }.toArray
    if x.productArity == tupleUnwrapped.length then Some(Tuple.fromArray(tupleUnwrapped).asInstanceOf[TupleInverseMap[T, Option]]) else None
  }

  def headOf[T <: Tuple](c: T): Option[Head[T]] = {
    c.productIterator.toList.headOption.map(_.asInstanceOf[Head[T]])
  }

  def secondOf[T <: Tuple](a: T): Option[Second[T]] = {
    a.productIterator.toList.lift(1).map(_.asInstanceOf[Second[T]])
  }
}

