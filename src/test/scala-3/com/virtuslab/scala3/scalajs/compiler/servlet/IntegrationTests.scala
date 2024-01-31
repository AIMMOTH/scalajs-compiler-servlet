package com.virtuslab.scala3.scalajs.compiler

import com.virtuslab.scala3.scalajs.compiler.servlet.TestContext
import org.scalatest.funsuite.AnyFunSuite

import java.net.URL
import java.util as ju
import java.io.{DefaultFileSystem, File, FileInputStream, FileNotFoundException, InputStream, WinNTFileSystem}
import org.scalajs.linker.StandardImpl
import org.scalajs.linker.interface.StandardConfig

class IntegrationTests extends AnyFunSuite: 
  testClass =>

  test("single file") {
    // Given
    val code = s"""
      |object Main:
      | def main(args: Array[String]) =
      |  println("A")
    |""".stripMargin

    val compiler: ScalaJsCompiler = ScalaJsCompiler()
    val servletCtx = new TestContext()
    System.out.println("Classloader root:" + servletCtx.getClassLoader().getResource("."))

    val libs = Set("scala3-library_3-3.3.1.jar", "scala-library-2.13.10.jar", "scalajs-javalib-1.12.0.jar", "scalajs-library_2.13-1.12.0.jar")
    val classpath = compiler.init(servletCtx, "", libs)

    // When
    val result = compiler.compileScalaJsString(classpath, code, Optimizer.Fast)

    // Then
    assert(result.nonEmpty)
  }