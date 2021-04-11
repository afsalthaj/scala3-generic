---
marp : true
---

## Scala3 - a sneak peak and a bit of macros


---
## By the end of the talk, we learn
1. Type computation, a few examples, Tuple wrap/unwrap, bounds etc
2. Polymorphic function types. Rank N in Scala language.
3. A scala2 style typeclass derivation with Scala3 tuples.
4. A few questions to listeners.
5. Mirror - A compiler primitive.
6. Inline - a couple of examples.
7. Type class derivation - A scala3 style :( :/ :)
8. Scala3 Macros. Basics, and more.

---

## Why Macros?

Macros are used to reduce duplication across constructs 
of the language, that are not ameanable to value based refactoring techniques.
(such as introducing varibale, function etc).

There are - loss of typesafety or loss of performance.

Value based mechanism are not always the answer for refactoring - sometimes
it can lead to typesafety pbms and performance issues.

---

## Take an example


```scala
// any attempt to abstract over this can reduce performance - massive performance penalty
def log(line: => String): Unit = 
 if (LoggingEnabled) println(line)

// Also used in parsing libraries - fastParse in scala2.

```

---

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

// Demo  

```

## Compile time computation

import scala.compiletime._
Singleton type functionality.

```scala
val something: false = false
val foo: "Foo" = "Foo"
val boo: "Boo" = "Foo" // is wrong
// false is a subtype of Boolean
// All inlines need to be of singleton types.
```

// Show Bound example

```scala
def succ[N <: Int]: Int = ???
```

## Scala3 style of typeclass derivation using inline.
Copy from John's implementation. For Show - but I personally feel its limited (using Mirror, derives, caching etc)

## Typeclass derivation in Scala2 using Scala3 tuples

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

## Scala3 Macros
Scala2 is so powerful - create code that has non-sensible types.
Scala3 macros is always strongly typed - the meaning of that code doesn't change through macros. 
It doesn't allow you to change the semantics of macros
These limitations are sensible. It doesn't change the  meaning of code. Reduction in the code.

You see `inline` the possiblity that right side is end up being a macro.
Macro is a fusion of `inline`, and the second feature?

The feature is called quotation and splicing.
The new syntax for them is. ${} meaning quotation. ' sign is called quotation.
$ is splicing.

What is quotation?
After parsing, raw text -> AST. Template declaration, and then declaration and so on and so forth.
This is in terms of the algebraic data type in the brain of scala compiler.
We can peek into this brain at the context of a scala3 macro, at compile time.

def assertV(expor: Boolean): Unit = ???
def assert(expr: Boolean): Unit = ??? // the code behind the boolean expression.
We don't want access to Boolean value at runtime. But whatever code that is being utilised 
to generate the boolean value.

assertV(1 > 0)
At runtime, this will produce a value of true.

But if we need to perform operations at compile time, that's what quotation is all about.
To access this code, quote it.!!!!

import scala.quoted._

Macros are evaluated at compile time --> inline
Expr[Boolean] === a model of a scala expression that produces a boolean variable.

Expr[Boolean] exists at compile time

// Feed in scala code and returns scala code
def assertImpl(expr: Expr[Boolean])(using Quotes): Expr[Any] = '{
    // inside a quotation we are splicing it.
  val evaluate: Boolean = $expr // evaluate the boolean variable
  val render: String = ${Expr(expr.show)} // Discuss why couldn't you do val render = expr.shows

  if (!evaluate) throw new AssertionError(s"Failed assertion: ${render}")

}'

Splicing is the inverse of quotation. It takes an Expr[A]
and turns it into a value of type `A`. This is the speciality of scala3 macros.
Its not any Expr[Any] as in Scala2.

```scala
inline def assert(inline expr: Boolean): Unit = 
  ${assertImpl('{expr})}

// Easier to model these in our mind
// ${expr: Expr[A]} : A
// '{expr: A}: Expr[A]
// splice(quote(a)): A
// quote(splice(expr)): Expr[A]
// More or less alebraic laws that governs the behaviour of splice and quotes.
// quote (alwaysAnA)
// splice(alwaysAnExpr)

```

