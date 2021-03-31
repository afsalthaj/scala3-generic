
import com.thaj.scala.three.ShapelessReImpl.Generic
import com.thaj.scala.three.TupleOps
import com.thaj.scala.three.TupleOps.unsafeSequence
import org.junit.Test
import org.junit.Assert._
import scala.deriving._

class TestTupleOps {
  @Test def headOf() = {
    val tuple: (String, Int) = "afsal" -> 1
    val headOfResult: Option[String] = TupleOps.headOf(tuple)
    assertEquals(headOfResult, Some("afsal"))
  }

  @Test def unsafeSequence() = {
    val result: Option[(Int, String)] = TupleOps.unsafeSequence((Some(1), Some("afsal")))
    assertEquals(result, Some(1 -> "afsal"))
  }

  // FIXME Make this compile
  //  @Test def testShapelessImpl() = {
  //    final case class Abc(a: String, b: Int, c: Double)
  //    val string = Generic[Abc].to(Abc("afsal", 1, 1.0))
  //
  //    assertEquals(string, ("afsal", 1, 1.0))
  //  }

  @Test def headOfProduct() = {
    final case class Bcd(b: String, c: Int, d: Double)
    val string: Option[String] = Generic.headOfProduct[Bcd](Bcd("afsal", 1, 2.0))
    assertEquals(string, Some("afsal"))
  }


  @Test def secondOfProduct() = {
    final case class Bcd(b: String, c: Int, d: Double)
    val string: Option[Int] = Generic.secondOfProduct[Bcd](Bcd("afsal", 1, 2.0))
    assertEquals(string, Some("afsal"))
  }
}
