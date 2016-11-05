package com.github.aimmoth.scala.compiler.jetty

import org.junit.Test

class TestCompiler {
  
    val source = 
      """
package com.github.aimmoth.scala.compiler.jetty


import scala.scalajs.js

import org.scalajs.dom

import com.github.aimmoth.scala.compiler.jetty.Optimizer;
import com.github.aimmoth.scala.compiler.jetty.ScalaJsCompiler;

import js.annotation._

@JSExport
class Foo(val x: Int) {
  override def toString(): String = s"Foo($x)"
}

@JSExportAll
object HelloWorld extends js.JSApp {

  def main(): Unit = {
    println("Hello world!")
  }
  
  def alerter(): Unit = {
    dom.window.alert("Hello!")
  }
}
    """
    
//  @Test
  def testCompilerFast : Unit = {
    val compiler = new ScalaJsCompiler
    val script = compiler.compileScalaJsString(getClass.getClassLoader, source, Optimizer.Fast, "", List("scalajs-dom_sjs0.6_2.11-0.9.1.jar"))
    println(script)
    println(s"Fast script size ${script.length}B")
  }    
    
//  @Test
  def testCompilerFull : Unit = {
    val compiler = new ScalaJsCompiler
    val script = compiler.compileScalaJsString(getClass.getClassLoader, source, Optimizer.Full, "", List("scalajs-dom_sjs0.6_2.11-0.9.1.jar"))
    println(s"Full script size ${script.length}B")
  }
  
  @Test
  def testCompilationError : Unit = {
    val bug =
      """
        import scala.scalajs.js
import js.annotation.JSExport
        
        object Buggish extends js.JSApp {
          @JSExport
          def main = println("Bug") + println("Me")
        }
      """
    try {
      val compiler = new ScalaJsCompiler
      compiler.compileScalaJsString(getClass.getClassLoader, bug, Optimizer.Fast, "")
    } catch {
      case e : Throwable =>
        println(e.getMessage)
        println("Bug found!")
    }
  }
}