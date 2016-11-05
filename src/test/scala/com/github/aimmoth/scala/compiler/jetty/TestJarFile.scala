package com.github.aimmoth.scala.compiler.jetty

import java.util.zip.ZipFile

import org.junit.Test

class TestJarFile {

  @Test
  def readJarFileWithScalaJsSource : Unit = {
    new ScalaJsCompiler match {
      case compiler =>
        getClass.getClassLoader.getResource("ScalaJsSource.jar") match {
          case path =>
            new ZipFile(path.getFile) match {
              case jarFile =>
                compiler.compileJarWithScalaJsSource(getClass.getClassLoader, jarFile, Optimizer.Fast, "") match {
                  case compiled =>
                    println(s"Compiled size ${compiled.length}B")
                }
            }
        }
    }
  }
}