## Why Macros?

Macros are used to reduce duplication across constructs 
of the language, that are not ameanable to value based refactoring techniques.
(such as introducing varibale, function etc).

There are - loss of typesafety or loss of performance.

Value based mechanism are not always the answer for refactoring - sometimes
it can lead to typesafety pbms and performance issues.

## Take an example


```scala
// any attempt to abstract over this can reduce performance - massive performance penalty
def log(line: => String): Unit = 
 if (LoggingEnabled) println(line)

// Also used in parsing libraries - fastParse in scala2.

```

## Lisp has powerful metaprogrammign capabilities

Example: Clojure is homoiconic language.

## Macros - CodeGen at compile time. Writing code that generates code

Thats the reason.

## Inline

// If not, inline invoking is through a generated `getter` in parent object
```scala
inline val LoggingEnabled = false
```

The above provides guaranteed inlining. Whatever that is on the right side,
will be copy pasted into th callsite.

## Inline

```scala

inline val LoggingEnabled = false

def log(line :=> String): Unit = 
 if (LoggingEnabled) println(line)
 // if (false) println(line)
 // and then just to unit , if LoggingEnabled is false (if values are known to be true or false)
 // Is that it? Not really.
 // 

```

```scala

inline def log(line: => String): Unit = ???

// That way, the alloocation to line is also gone away.

```

In Scala3, values are known to be boolean scala3

How many allocations are involved in executing a call to log?
1 allocation through byname parameters. Function0, to stuff this String - lazily.
1 call to getter of LoggingEnabled. which is then called to Jvm function __getField as well.


## Inline function can be recursive

```scala
inline def pow(num: Float, exp: Int): Float = 
 if(exp == 0) 1.0F else num * pow(num, exp - 1)

pow(10.5F, 3) // Large amount of code thats known at compile time.

pow(10.5F, 100) // Too much of code generation - that's going to bring a compile time error

```

## Inline + Transparent
```scala
// Tranbsparent allows the compiler to see through the types
// early if its able to resolve the branches at compile time
transparent inline def apply(v: Int): Any = 
  inline if (v >= 0) new Natural(v) else error("Not a natural number")

```

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