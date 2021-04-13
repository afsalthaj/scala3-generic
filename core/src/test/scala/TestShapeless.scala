
import org.junit.Test
import org.junit.Assert._
import com.thaj.scala.three.shapeless.ops.tuple._
import com.thaj.scala.three.shapeless.Generic
import com.thaj.scala.three.shapeless.typeclasses.{Equal, Head, Second, MapperBasic, Mapper, Case, MapperF}

import scala.deriving._

class TestShapeless {
  @Test def headOfTuple() = {
    val tuple: (String, Int) = "afsal" -> 1

    val result: String = headOf(tuple)

    assertEquals(result, "afsal")
  }

  @Test def unsafeSequenceTuple() = {
    val result: Option[(Int, String)] =
      unsafeSequence((Some(1), Some("afsal")))

    assertEquals(result, Some(1 -> "afsal"))
  }

  @Test def testGenericRepr() = {
    final case class A(a: String, b: Int, c: Double)

    val string: (String, Int, Double) =
      summon[Generic[A]].to(A("afsal", 1, 1.0))
  
    assertEquals(string, ("afsal", 1, 1.0))
  }

  @Test def testHeadOfProduct() = {
    final case class A(b: String, c: Int, d: Double)

    val string: String = Head[A].head(A("afsal", 1, 2.0))

    assertEquals(string, "afsal")
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

  @Test def testMapperBasicOfTuple() = {
    import MapperBasic._

    given MapperBasic.Case.Aux[Int, Int] = MapperBasic.Case.createInstance(i => i * i)
    given MapperBasic.Case.Aux[Double, Double] = MapperBasic.Case.createInstance(i => i * i)
    
    val tuple = (2, 4.0)
    val result: (Int, Double) = tuple.mapElements

    assertEquals(result, (4, 16.0))
     
  }

  @Test def testMapper() = {
    import Mapper._

    object Poly {
        given Case.Aux[this.type, Int, Int] = Case.createInstance(i => i * i)
        given Case.Aux[this.type, Double, Double] = Case.createInstance(i => i * i)
    }

    val tuple = (2, 4.0)
    val result: (Int, Double) = tuple.mapElements(Poly)

    assertEquals(result, (4, 16.0))
  }

  @Test def testTraverse() = {
    import MapperF._

    object optional {
      given Case.Aux[this.type, Int, Option[Int]] = 
        Case.createInstance(i => if (i < 10) None else Some(i))

      given Case.Aux[this.type, Double, Option[Double]] = 
        Case.createInstance(i => if (i > 100) None else Some(i))
    }

    val tuple = (20, 50.1)

    assertEquals(tuple.traverse(optional), Some((20, 50.1)))
  }

  @Test def traverseIdentitySequence() = {
    import MapperF._

    // Based on the idea that sequence is traverse identity
    object identity {
      given id[F[_], A]: Case.Aux[this.type, F[A], F[A]] = 
        Case.createInstance(i => i)
    }

    val tuple: (Option[Int], Option[Double]) = (Some(20), Some(50.1))
    val result: Option[(Int, Double)] = tuple.traverse(identity)

    // didn't compile yet, but work with concrete types of tuple.
    // def sequence[T <: Tuple, F[_]: Monad](tuple: TupleMap[T, F]): F[TupleInverseMap[TupleMap[T, F], F]] = 
    //   tuple.traverse(identity)
    
    assertEquals(result, Some((20, 50.1)))
  }
}
