
import org.junit.Test
import org.junit.Assert._
import com.thaj.scala.three.shapeless.ops.tuple._
import com.thaj.scala.three.shapeless.Generic
import com.thaj.scala.three.shapeless.typeclasses.{Equal, Head, Second}

import scala.deriving._

class TestShapeless {
  @Test def headOfTuple() = {
    val tuple: (String, Int) = "afsal" -> 1

    val result: Option[String] = headOf(tuple)

    assertEquals(result, Some("afsal"))
  }

  @Test def unsafeSequenceTuple() = {
    val result: Option[(Int, String)] =
      unsafeSequence((Some(1), Some("afsal")))

    assertEquals(result, Some(1 -> "afsal"))
  }

  @Test def testGenericRepr() = {
    final case class A(a: String, b: Int, c: Double)

    val string: (String, Int, Double) =
      Generic[A].to(A("afsal", 1, 1.0))
  
    assertEquals(string, ("afsal", 1, 1.0))
  }

  @Test def testHeadOfProduct() = {
    final case class A(b: String, c: Int, d: Double)

    val string: Option[String] = Head[A].head(A("afsal", 1, 2.0))

    assertEquals(string, Some("afsal"))
  }

  @Test def testSecondOfProduct() = {
    final case class A(b: String, c: Int, d: Double)

    val int: Option[Int] = Second[A].second(A("afsal", 1, 2.0))

    assertEquals(int, Some(1))
  }

  @Test def testEqualOfProduct() = {
    final case class A(b: String, e: Int)

    val boolean = Equal[A].eq(A("afsal", 1), A("afsal", 1))

    assertEquals(true, boolean)
  }
}
