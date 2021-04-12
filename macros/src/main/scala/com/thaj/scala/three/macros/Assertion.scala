package com.thaj.scala.three.macros

import scala.quoted._

// Splicing
// Quoting
// And don't miss runtime code with compile time code. Phase consistency principle.
// This in short implies, all your runtime-like representation should be an output of splice.
// Or in other words, the code that you generate through macros, shouldn't have anything related to `expr` (AST)
// scala disallows it. Coz runtime can never know anything about `expr`. So always lift it to an expression using
// Expr (remember this is unlike quoting) such that at compile time u get the evaluated result of expression (ex: String)
// and obviously if you are referring this elsewhere u have to splice it further. 
object Macros {
    inline def assert(expr: => Boolean): Unit = 
      ${assertImpl('{expr})}

    def assertImpl(expr: Expr[Boolean])(using Quotes): Expr[Any] = '{
       val result = ${expr}
       val render = ${Expr(expr.show)}

       if (!result) throw new AssertionError(s"Failed insertion. ${render}")
    }

    // inspect(expr.toString) will not
    inline def inspect(inline x: Any): Any = ${ printAnyExpr('x) }

    def printAnyExpr[A](expr: Expr[A])(using Quotes): Expr[Any] = {
       val string = expr.show
       if (string.contains("toString")) scala.sys.error("Cannot call toString in your expressions")
       expr
    }

    /**
     *  Macros.nonEmpty0("afsal") // compiles
     *  Macros.nonEmpty0("") // doesn't compile
     *  val x = "afsal"
     *  Macros.nonEmpty0(x) // doesn't compile, because expr.valueOrError is gonna give error
     */ 
    inline def nonEmpty0(expr: => String): String = 
      ${ nonEmptyString0Impl('expr) }

    def nonEmptyString0Impl(expr: Expr[String])(using Quotes): Expr[String] = {
      val result = expr.valueOrError 
      if result.trim.isEmpty then scala.sys.error("Empty String")
      expr
    }

    trait Show[A] {
       def show(a: A): String
    }

    object Show {
       final case class Secret(password: String)

       given Show[Boolean] with
         def show(b: Boolean) = b.toString
       
       given Show[Double] with
         def show(b: Double) = b.toString

       given Show[Secret] with 
         def show(s: Secret) = "***"
       
       given Show[String] with
         def show(s: String) = s  
    }

    extension (inline sc: StringContext)
       inline def ss(inline args: Any*): String = ${ showMeImpl('sc, 'args) }

    def showMeImpl(s: Expr[StringContext], argsExpr: Expr[Seq[Any]])(using Q: Quotes): Expr[String] = {
       import quotes.reflect.report

       argsExpr match {
          case Varargs(argExprs) => {
           val argShowedExpression = argExprs map { a => 
              a match {
                 // Precise types
               case '{${arg}: t} =>
                 Expr.summon[Show[t]] match {
                    case Some(showExpr) =>'{ ${showExpr}.show(${arg}) }
                    case None => report.error(s"Could not find implicit value for show for ${Type.show[Show[t]]}", arg); '{???}
                 }
               }
            }
            
            val newArgsExpr = Varargs(argShowedExpression)
           
           '{ $s.s($newArgsExpr: _*) }
          }
          case _ => 
              report.error(s"Args must be explicit", argsExpr)
             '{???}
       }

    }  

    final case class TopicName(organisation: String, teamName: String, projectName: String, suffix: String)

}
