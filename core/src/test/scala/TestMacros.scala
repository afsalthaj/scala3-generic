import org.junit.Test
import org.junit.Assert._
import com.thaj.scala.three.shapeless.ops.tuple._
import com.thaj.scala.three.shapeless.Generic
import com.thaj.scala.three.shapeless.typeclasses.{ Equal, Head, Second }

import com.thaj.scala.three.shapeless.bounds._

import com.thaj.scala.three.macros._
import scala.util._

class TestMacros {
  @Test def testMacros() = {
    val someExpr = 1 < 0
    val result = Try { Macros.assert(someExpr) } match {
      case Success(_)     => assert(false)
      case Failure(error) => assert(error.getMessage.contains("someExpr"))
    }
  }

  @Test def nonEmptyString() = {
    val x = Macros.nonEmpty0("afsal")
    // val z = "afsal"
    // Macros.nonEmpty0(z) doesn't compile
    assertEquals(x, "afsal")
  }

  @Test def testMyStringInterpolation() = {
    import Macros._
    val boolean: Boolean = true
    val x                = ss"true afsal ${ val s = true; s }"

    assertEquals(x, "true afsal true")
  }
}
