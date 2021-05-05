package com.thaj.scala.three.shapeless.smarconstructors

class Natural(v: Int) {
  override def toString: String = v.toString
}

object Natural:
  transparent inline def apply(v: Int): Either[String, Natural]  = 
     if (v > 0) Right(new Natural(v)) else Left("failed")
end Natural

object x:
  val x = Natural(1)

