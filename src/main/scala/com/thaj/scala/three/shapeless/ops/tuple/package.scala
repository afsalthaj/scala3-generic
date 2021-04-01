package com.thaj.scala.three.shapeless.ops

import com.thaj.scala.three.shapeless.TupleTypes.{Head, Second, TupleInverseMap}

package object tuple {
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
