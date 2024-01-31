package com.github.aimmoth.scalajs.compiler.servlet

import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import scala.io.Source
import scala.language.postfixOps
import scala.reflect.io.VirtualFile
import java.util.logging.Logger
import org.scalajs.core.tools.logging.{ Logger => JsLogger, Level => JsLevel }

class ScalaJsCompiler {

  val log = Logger.getLogger(getClass.getName)
  var classpath : Classpath = null

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

  def compileScalaJsString(source: String, optimizer: Optimizer, charsetName: String = "UTF-8"): String = {
    compileScalaJsStrings(List(source), optimizer)
  }
  def compileScalaJsUtf8StringFast(source: String): String = compileScalaJsStrings(List(source), Optimizer.Fast)
  def compileScalaJsUtf8StringFull(source: String): String = compileScalaJsStrings(List(source), Optimizer.Full)

  /**
   * String with Scala JS code
   */
  def compileScalaJsStrings(sources: List[String], optimizer: Optimizer, charsetName: String = "UTF-8", minLevel: JsLevel = JsLevel.Info): String = {

    if (classpath == null) {
      throw new RuntimeException("Run init to load classpath files first!")
    }

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

    val files = sources.map(s => makeFile(s.getBytes(charsetName)))

    new CompileActor(classpath, files, optimizer, minLevel).doCompile match {
      case cr if cr.jsCode.isDefined =>
        cr.jsCode.get
      case cr => {
        val text = cr.log
        log.warning(text)
    	  throw new Exception(text)
      }
    }
  }

  /**
   * Call this method first to load all libraries. Loader could be reading local files:
   * <pre>
   *   // Read all jars file paths on classpath
   *   val classpath = System.getProperty("java.class.path").split(";")
   *   val loader : (String => InputStream) = (jarFilename: String) => {
   *     classpath.find(s => s.endsWith(jarFilename)) match {
   *       case Some(found) => {
   *         println("Found on classpath:" + found)
   *         new FileInputStream(new File(found))
   *       }
   *       case None => throw new FileNotFoundException(jarFilename)
   *     }
   *   }
   * </pre>
   *
   * @param loader In a Servlet context it could be <pre>(jarFile:String) => ServletContext.getResourceAsStream(jarFile)</pre>
   * @param relativeJarPath Use "" if running locally, or "/WEB-INF/lib/" when loading from Servlet context
   * @param additionalLibs Any provided jar from a dependency. For instance "scalajs-dom_sjs0.6_2.11-0.9.8.jar"
   * @param baseLibs Default is Seq("scala-library-2.11.12.jar", "scala-reflect-2.11.12.jar", "scalajs-library_2.11-0.6.33.jar")
   */
  def init(loader: (String) => InputStream, relativeJarPath: String, additionalLibs : Set[String] = Set(), baseLibs: Seq[String] = Seq("scala-library-2.11.12.jar", "scala-reflect-2.11.12.jar", "scalajs-library_2.11-0.6.33.jar")): Unit = {
    classpath = Classpath(loader, relativeJarPath, baseLibs, additionalLibs)
  }
}