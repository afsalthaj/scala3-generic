package com.thaj.scala.three.shapeless.macros

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

}