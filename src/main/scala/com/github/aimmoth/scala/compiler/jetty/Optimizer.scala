package com.github.aimmoth.scala.compiler.jetty

sealed abstract class Optimizer

object Optimizer {
  case object Fast extends Optimizer

  case object Full extends Optimizer
}