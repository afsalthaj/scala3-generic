## Defining remove from tuples?

How do we do that?

Let's say Remover => an implicit proof.
For a given type, prove this can be removed.
In the process of proving it, do the actual removal.
Obviosuly dependent output type - while it impose some restrictions such as it can't be used in computational computation,
we are helping the compiler to stop searching around, and potentially helping out the users to stop messing around computing the type by themselves.
Howver u get over this limitation using Aux type where it push the type back to the type parameter position.a


## Typeclass derivation in Scala3

There are compiler primitives in Scala3.

1. Mirror
2. derives clauses
3. convention driven type class derived interface

Can we use it? Well may be. 

Library support
shapless 3 preview for scala3

## Dotty Mirrors
Compiler intrinsic for ADTs (pr, cr, and enums)
Supports ADTs of all kinds
intentionally low level

```scala
trait Mirror {

}

trait Sum extends Mirror {
  def ordinal(x: MirroredMonoType): Int
}

triat Product extends Mirror {
    def fromProduct(p. scala.Product): MirroredMonoType

}
```

## Mirror example

case class Abc(x: Int, y: String)

implicitly[Mirror{type MirroredType = Abc}]

Mirror.Product {
  MirroredType = Abc
  MirroedMonoType = Abc
  MirroredLabel = "ISB"
  MirroredElemTypes = (Int, String)
  MirroedElemLabels = ("x", "y")

}

MirroredMonoType is existentially quantified version of MirroredType. 
Bsically, what's the most general fully applied type that you can get given u have higher kinded MirroredType.


# Mirror example for Coproducts

sealed trait Option[+A]
case class Some[A](value: A) extends Option[A]
case object None extends Option[Nothing]

implicitly[Mirror {type MirroredType = Option}]

Mirror.Product {
    MirroredType = Option
    MirroredMonoType = Option[_]
    MirroedLabel = "Option"
    MirroredElemTypes = [+T] =>> (Sm[T], None.type)
    MirroredElemLabels = ("Some", "None")
}

implicitly[Mirror {type MirroredType = Sm}]

Mirror.Product {
    MirroredType = Sm
    MirroredMonoType = Sm[_]
    MirroedLabel = "Sm"
    MirroredElemTypes = [+T] =>> Tuple1[T]
    MirroredElemLabels = Tuple1["value"]
}

implicitly[Mirror {type MirroredType = Nn.type}]

Mirror.Product {
    MirroredType = Nn.type
    MirroredMonoType = Nn.type
    MirroedLabel = "Nn"
    MirroredElemTypes = Unit
    MirroredElemLabels = Unit
}

implicitly[Mirror {type MirroredType = Option[Int]}]

Mirror.Product {
    MirroredType = Option[Int]
    MirroredMonoType = Option[Int]
    MirroedLabel = "Option"
    MirroredElemTypes =  (Some[Int], None.type)
    MirroredElemLabels = ("Some", "None")
}

# No additional cost of having these around compared to what's in shapeless.

Runtime footprint => big win compared to shapeless. Thats coz all the representation
is just companion object cast.

# Derives clauses - which is in fact scala3 way of doing it

```scala
case class Abc(x: Int, y: Int) derives Show

object Show {
  def derived[T](m: Mirror[T]): Show[T] = ???
}
```

What is compiler doing?

-> Caching the implicit instance
-> Not repeating - that's all handled automatically.

```scala
 object Abc extends Mirror.Product {
   implicit val derivedShow: Show = Show.derived(Abc)
 }

```

# Shapeless3

Miles just came up with Generic that is Poly-kinded.
Generic:
The difference is its just a type alias to Mirror.
Its all implemented in terms of Mirror.

Instances:
Poly kinded
Implemented in terms of Genric

```
type ProductGeneric[T] {
  type Repr = MirroredElemTypes
  def toRepr(t: T): Repr
  def fromRepr(r: Repr): T
}

type CoproductGeneric[T] {
  type Repr = ToUnion[MirroredElemeTypes] (// type matching )
  def toRepr(t: T): Repr = t
  def fromRepr(r: Repr): T = t
}

case class ISB(i: Int, s: String, b: Boolean)

// From scaladays 2019 - Miles

```

# Shapeless * -> *
```
type ProductGeneric[T[_]] {
  type Repr = MirroredElemTypes
  def toRepr[A](t: T[A]): Repr[A]
  def fromRepr[A](r: Repr[A]): T
}

and supports * -> * -> *
// better compile time performance. and bytecode footproint
// Captures common folds - Eliminates the induction boilerplate for "monoidal" type classes
```