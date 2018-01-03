package com.github.aimmoth.scala.compiler.jetty

import java.util.zip.ZipEntry
import java.util.zip.ZipFile

import scala.io.Source
import scala.language.postfixOps
import scala.reflect.io.VirtualFile

import javax.servlet.ServletContext
import java.util.logging.Logger

class ScalaJsCompiler {

    val log = Logger.getLogger(getClass.getName)

    def compileJarWithScalaJsSource(context : ServletContext, jarWithSource: ZipFile, optimizer: Optimizer, relativeJarPath: String): String = {
    jarWithSource.entries match {
      case entries => 
        (new Iterator[ZipEntry] {
          def next = entries.nextElement
          def hasNext = entries.hasMoreElements
        }).filter(entry => entry.getName.endsWith(".scala"))
        .map(entry => Source.fromInputStream(jarWithSource.getInputStream(entry)).mkString)
        .mkString match {
          case source =>
            compileScalaJsString(context, source, optimizer, relativeJarPath)
        }
    }
  }
  
  def compileScalaJsString(context : ServletContext, source: String, optimizer: Optimizer, relativeJarPath: String, additionalLibs : Set[String] = Set()): String = {
    compileScalaJsStrings(context, List(source), optimizer, relativeJarPath, additionalLibs)
  }
  
  /**
   * String with Scala JS code
   */
  def compileScalaJsStrings(context : ServletContext, sources: List[String], optimizer: Optimizer, relativeJarPath: String, additionalLibs : Set[String] = Set()): String = {
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

    val actor = new CompileActor(Classpath(context, relativeJarPath, additionalLibs), "scalatags", files, optimizer)
    actor.doCompile match {
      case cr if cr.jsCode.isDefined =>
        cr.jsCode.get
      case cr => {
        val text = cr.log
        log.warning(text)
    	  throw new Exception(text)
      }
    }
  }

}