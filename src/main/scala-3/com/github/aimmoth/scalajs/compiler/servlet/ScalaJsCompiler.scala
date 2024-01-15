package com.aimmoth.scalajs.compiler.servlet

import java.util.zip.ZipEntry
import java.util.zip.ZipFile

import scala.io.Source
import scala.language.postfixOps
import dotty.tools.io.VirtualFile

import javax.servlet.ServletContext
import java.util.logging.Logger

class ScalaJsCompiler {

  val log = Logger.getLogger(getClass.getName)
  var classpath: Classpath = null

  def compileJarWithScalaJsSource(jarWithSource: ZipFile, optimizer: Optimizer): String = {
    jarWithSource.entries match {
      case entries => 
        (new Iterator[ZipEntry] {
          def next = entries.nextElement
          def hasNext = entries.hasMoreElements
        }).filter(entry => entry.getName.endsWith(".scala"))
        .map(entry => Source.fromInputStream(jarWithSource.getInputStream(entry)).mkString)
        .mkString match {
          case source =>
            compileScalaJsString(source, optimizer)
        }
    }
  }
  
  def compileScalaJsString(source: String, optimizer: Optimizer): String = {
    compileScalaJsStrings(List(source), optimizer)
  }
  
  /**
   * String with Scala JS code
   */
  def compileScalaJsStrings(sources: List[String], optimizer: Optimizer): String = {
    /**
     * Converts a bunch of bytes into Scalac's weird VirtualFile class
     */
    def makeFile(src: Array[Byte]) = {
      val singleFile = new VirtualFile("ScalaFiddle.scala")
      val output = singleFile.output
      output.write(src)
      output.close()
      singleFile
    }

    val files = sources.map(s => makeFile(s.getBytes("UTF-8")))
    
    if (classpath == null) {
      throw new RuntimeException("Run init to load classpath files first!")
    }

    new CompileActor(classpath, "scalatags", files, optimizer).doCompile match {
      case cr if cr.jsCode.isDefined =>
        cr.jsCode.get
      case cr =>
        val text = cr.log
        log.warning(text + cr)
        throw new Exception(text)
    }
  }

  def init(context : ServletContext, relativeJarPath: String, additionalLibs : Set[String] = Set()) = {
    classpath = Classpath(context, relativeJarPath, additionalLibs)
  }
}