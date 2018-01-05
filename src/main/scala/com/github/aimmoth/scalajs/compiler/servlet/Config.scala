package com.github.aimmoth.scalajs.compiler.servlet

import java.util.Properties

object Config {
  
  val clientFiles = List("/client-fastopt.js")

  val environments = Map("default" -> List("dom", "scalatags", "async"))

  val extJS = List("https://ajax.googleapis.com/ajax/libs/jquery/2.2.2/jquery.min.js")

  val libCache = "target/extlibs"

  val scalaVersion = "2.11.12"
  val scalaMainVersion = scalaVersion.split('.').take(2).mkString(".")
  val scalaJSVersion = "0.6.21"
  val scalaJSMainVersion = scalaJSVersion.split('.').take(2).mkString(".")
}

