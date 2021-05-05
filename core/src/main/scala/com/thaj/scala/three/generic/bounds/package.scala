package com.thaj.scala.three.generic

import scala.compiletime.S

package object bounds {  
  // Works with only RC1
  type Bound[Min <: Int, Max <: Int] <: Int = Max match {
    case Min => Min
    case S[m] => Max | Bound[Min, m]
  }

  type Bound0 = 1 | 2 | 3 | 4 | 5

  val int0: Bound0 = 5

  val int: Bound[1, 10] = 10

  // Well let's revisit our factorial, and what really went wrong

  def partFactorial(f: Int => Int): Int => Int = n => if (n == 0) 1 else n * f(n - 1)
  // well if f was partFactorial itself then the above expr is a factorial

  // def factorial = partFactorial(partFactorial _) // well partFactorial _ is of the type `Int => Int => Int => Int`.
  // all that we needed was Int => Int. Ok, let's hack off. We have Any in scala, and see how it goes.

   def partFactorialv0(self: Any): Int => Int = n => if (n == 0) 1 else n * self.asInstanceOf[Any => (Int => Int)](self)(n - 1)
   def factorial(n: Int) = partFactorialv0(partFactorialv0 _)
   // factorial(3) === 6

   // we need to derive almostFactorial out of it, and come up with a y combinator

   def almostFactorial(f: Int => Int): Int => Int = 
     n => if (n == 0) 1 else n * f(n-1)

  def partFactorialv1(self: Any): Int => Int = {
    val f = self.asInstanceOf[Any => (Int => Int)](self) // val f = self(self) 
    n => if (n == 0) 1 else n * f(n - 1) // almost n factorial
  }

  def partFactorialv2(self: Any): Int => Int = {
    val f = self.asInstanceOf[Any => (Int => Int)](self) // val f = self(self) 
    n => if (n == 0) 1 else n * f(n - 1) // almost n factorial
  } 

  def partFactorialv3(self: Any): Int => Int = {
    val selfSelf = self.asInstanceOf[Any => (Int => Int)](self) // val f = self(self) 
    almostFactorial(selfSelf) // almost n factorial
  }
  
  def factorialV3 = partFactorialv3(partFactorialv3 _) // stack safety issues come into picture here, hence factorialV3(4) doesn't work

  // Let's get rid of partFactorialV3(partFactoriv3 _), let's merge all of these into factorial

  def partFactorialv4: Any => (Int => Int) = {
    (self: Any) => almostFactorial(self.asInstanceOf[Any => (Int => Int)](self)) // almostFactorial self self
  }

  def factorialV4: Int => Int = {
    val partFactorial4 = 
       (self: Any) => almostFactorial(self.asInstanceOf[Any => (Int => Int)](self)) // almostFactorial self self

    partFactorial4(partFactorial4)   
  }

  def factorialV5: Int => Int = {
    val x = 
       (self: Any) => almostFactorial(self.asInstanceOf[Any => (Int => Int)](self)) // almostFactorial self self

    x(x)   
  }

  // The trick {val x: A = a; x(x) } === ((x: A) => x(x))(a)
  def factorialV6: Int => Int = {
    ((x: Any => (Int => Int)) => x(x))(
      (self: Any) => almostFactorial(self.asInstanceOf[Any => (Int => Int)](self))
    )
  }

  // Just renaming self , doesn't change the meaning of the program, since we are applying lambda twice.
  
  def factorialV7: Int => Int = {
    ((x: Any => (Int => Int)) => x(x))(
      (x: Any) => almostFactorial(x.asInstanceOf[Any => (Int => Int)](x))
    )
  }

  def makeRecursive(f: (Int => Int) => (Int => Int)) = {
        ((x: Any => (Int => Int)) => x(x))(
      (x: Any) => f(x.asInstanceOf[Any => (Int => Int)](x))
    )
  }

  def factorialV8 = makeRecursive(almostFactorial)

  // well make recursive is our y combinator def Y(f: (A => B) => (A => B)): A => B = f(a => Y(f)(a)), well we need to prove though.

  def y2(f: (Int => Int) => (Int => Int)) = {
        ((x: Any => (Int => Int)) => x(x))(
      (x: Any) => f(x.asInstanceOf[Any => (Int => Int)](x))
    )
  }

  def factorialV9 = y2(almostFactorial)

  def y3 = 
    (f: (Int => Int) => (Int => Int)) => {
       ((x: Any => (Int => Int)) => x(x))(
         (x: Any) => f(x.asInstanceOf[Any => (Int => Int)](x))
        )
  }


  def y4 = 
    (f: (Int => Int) => (Int => Int)) => {
       (
         (x: Any) => f(x.asInstanceOf[Any => (Int => Int)](x))
        )(
         (x: Any) => f(x.asInstanceOf[Any => (Int => Int)](x))
        )
  }

  // That is similar y f = (lambdaXfxx)((lambdaXfxx))

  // Now apply the first lambda expression to its argument, which is the second lambda expression, to get this:

  /**
   *   (Y f) = ((lambda (x) (f (x x))) (lambda (x) (f (x x))))
   *         = (f ((lambda (x) (f (x x))) (lambda (x) (f (x x)))))
   */
  def y5 = 
    (f: (Int => Int) => (Int => Int)) => {
      val lambdaXFxx = ((x: Any) => f(x.asInstanceOf[Any => (Int => Int)](x)))
      f(lambdaXFxx(lambdaXFxx))
  }

  // Proof that y5 is same as our y function
  // y f = (lambdaXfxx)((lambdaXfxx))
  // y f = f (y f), but this y5 doesn't have free variables - its a real y combinator, and is proven to be same as 
  // one that has free variables.

  def factorialV10: Int => Int = y5(almostFactorial) // doesn't work yet coz of stack overflow. Try urself. 

  // To get rid of stack overflow problem, 
  // may be we can apply the lambda technique we used before i.e, { val x: A => A = f(z) } is same as {val x : A => A = a => f(z)(a)}

  // All thats done here is in y5, apply an extra lambbda, wherever possible.


  def y6 = 
    (f: (Int => Int) => (Int => Int)) => {
      val lambdaXFxx = ((x: Any) => f((y: Int) => x.asInstanceOf[Any => (Int => Int)](x)(y)))
      f(lambdaXFxx(lambdaXFxx))
  }

  def factorialV11: Int => Int = y6(almostFactorial) // doesn't work yet coz of stack overflow. Try urself. 

  // Well, let's generalise y6
  def y8[A, B] = 
    (f: (A => B) => (A => B)) => {
      val lambdaXFxx = ((x: Any) => f((y: A) => x.asInstanceOf[Any => (A => B)](x)(y)))
      f(lambdaXFxx(lambdaXFxx))
  }

  def factorialV12: Int => Int = y8(almostFactorial) // doesn't work yet coz of stack overflow. Try urself. 

  // By this time, you can safely call it a day. But if you really want to push yourself a bit more, may be do this.

  // Well, what does that `Any` really mean. We need to let the compiler know that its always going to be from some type such that 
  // there exists for All `Z`, there exists A => B
  def y9[A, B] = 
    (f: (A => B) => (A => B)) => {
      val lambdaXFxx:[Z] => Z => (A => B) = ([Z] => (z: Z) => f((y: A) => z.asInstanceOf[Z => (A => B)](z)(y)))
      f(lambdaXFxx(lambdaXFxx))
  }

  def factorialV13: Int => Int = y9(almostFactorial) // doesn't work yet coz of stack overflow. Try urself. 

}

object X extends App {
  def y9[A, B] = 
    (f: (A => B) => (A => B)) => {
      val lambdaXFxx:[Z] => Z => (A => B) = ([Z] => (z: Z) => f((y: A) => z.asInstanceOf[Z => (A => B)](z)(y)))
      f(lambdaXFxx(lambdaXFxx))
  }


// I think I liked that one
def y[A, B] = 
  (f: (A => B) => (A => B)) => {
    lazy val lambda:[Z] => Z => (A => B) = 
      ([Z] => (z: Z) => f((y: A) => lambda(z)(y)))
    f(lambda(lambda))
  }


  def almostFactorial(f: Int => Int): Int => Int = 
     n => if (n == 0) 1 else n * f(n-1)

  def factorialV13: Int => Int = y(almostFactorial) // doesn't work yet coz of stack overflow. Try urself. 

  println(factorialV13(3))

}