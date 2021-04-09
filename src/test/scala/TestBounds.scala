
import org.junit.Test
import org.junit.Assert._
import com.thaj.scala.three.shapeless.ops.tuple._
import com.thaj.scala.three.shapeless.Generic
import com.thaj.scala.three.shapeless.typeclasses.{Equal, Head, Second}

import com.thaj.scala.three.shapeless.bounds._

class TestBounds {
  @Test def testBoundsMax() = {
    val bounded: Bound[0, 5] = 5
    val int: Int = bounded

    assertEquals(int, 5)
  }

  @Test def testBoundsMin() = {
    val bounded: Bound[0, 5] = 0
    val int: Int = bounded

    assertEquals(int, 0)
  }

  @Test def testBoundsInBetween() = {
    val bounded: Bound[0, 3] = 2
    val int: Int = bounded

    assertEquals(int, 2)
  }
}
