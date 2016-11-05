package com.github.aimmoth.scala.compiler.jetty

case class EditorAnnotation(row: Int, col: Int, text: Seq[String], tpe: String)

case class CompilerResponse(jsCode: Option[String], annotations: Seq[EditorAnnotation], log: String)
