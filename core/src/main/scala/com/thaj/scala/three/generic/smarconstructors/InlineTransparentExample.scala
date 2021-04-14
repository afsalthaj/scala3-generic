package com.thaj.scala.three.shapeless.smarconstructors

class Natural(v: Int) {
  override def toString: String = v.toString
}

object Natural:
   transparent inline def apply(v: Int): Option[Natural]  = 
     if (v > 0) Some(new Natural(v)) else None
end Natural
