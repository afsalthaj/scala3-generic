package com.thaj.scala.three.shapeless

import scala.compiletime.S

package object bounds {  
  type Bound[Min <: Int, Max <: Int] <: Int = Max match {
    case Min => Min
    case S[m] => Max | Bound[Min, m]
  }
}
