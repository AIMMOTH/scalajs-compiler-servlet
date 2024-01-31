package com.github.aimmoth.scalajs.compiler.servlet

import org.junit.jupiter.api.{Assertions, DisplayName, Test}
import org.scalajs.core.tools.logging.Level

import java.io.{File, FileInputStream, FileNotFoundException, InputStream}

class TestScalaJsCompiler {

  private val scalaJsCode =
    """
      |package tutorial.webapp
      |
      |object TutorialApp {
      |  def main(args: Array[String]): Unit = {
      |    println("Hello world!")
      |  }
      |}
      |""".stripMargin

  @DisplayName("Initiate ScalaJsCompiler and compile simple code")
  @Test
  def test = {
    // Given
    val allJarsOnClassPathSeparated = System.getProperty("java.class.path")
    val separator = System.getProperty("path.separator");
    val jarsWithFullFilePath = allJarsOnClassPathSeparated.split(separator)

    val loader : (String => InputStream)= (jarFile: String) => {
      jarsWithFullFilePath.find(s => s.endsWith(jarFile)) match {
        case Some(found) => {
          println("Found on classpath:" + found)
          new FileInputStream(new File(found))
        }
        case None => throw new FileNotFoundException(jarFile)
      }
    }

    val compiler = new ScalaJsCompiler
    val relativeJarPath = "" // We get full path from above and do not need a relative path
    val additionalLibs = Set[String]() // We don't have any extra dependencies in this repository
    val baseLibs = Seq("scala-library-2.11.12.jar", "scala-reflect-2.11.12.jar", "scalajs-library_2.11-0.6.33.jar") // Always needed for compilation
    compiler.init(loader, relativeJarPath, additionalLibs, baseLibs)

    // When
    val fastCompilationNotMinimized = Optimizer.Fast
    val charsetName = "UTF-8"
    val compilerLoggingLevel = Level.Info
    val result = compiler.compileScalaJsStrings(List(scalaJsCode), fastCompilationNotMinimized, charsetName, compilerLoggingLevel)

    // Then
    Assertions.assertNotNull(result)
    println("Javascript length:" + result.length)
  }
}
