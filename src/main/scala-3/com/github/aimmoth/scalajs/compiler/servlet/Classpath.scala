package com.aimmoth.scalajs.compiler.servlet

import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream

import scala.collection.mutable
import scala.concurrent.duration.DurationInt
import dotty.tools.io.Streamable
import dotty.tools.io.VirtualDirectory

import javax.servlet.ServletContext
import java.util.logging.Logger
import org.scalajs.linker.interface.IRFileCache
import org.scalajs.linker.StandardImpl
import scala.concurrent.ExecutionContext
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.scalajs.linker.interface.unstable.IRContainerImpl
import org.scalajs.linker.standard.MemIRFileImpl
import scala.concurrent.Future
import org.scalajs.linker.interface.IRFile
import dotty.tools.dotc.config.PathResolver
import dotty.tools.dotc.classpath.ClassPathFactory
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.Contexts.ContextBase

object Classpath {

  private lazy val build: (ServletContext, String, Set[String]) => Classpath = (klass, relativeJarPath, additionalLibs) => new Classpath(klass, relativeJarPath, additionalLibs)

  def apply(context: ServletContext, relativeJarPath: String, additionalLibs: Set[String] = Set()) = build(context, relativeJarPath, additionalLibs)
}

/**
 * Loads the jars that make up the classpath of the scala-js-fiddle
 * compiler and re-shapes it into the correct structure to satisfy
 * scala-compile and scalajs-tools
 */
class Classpath(context: ServletContext, relativeJarPath: String, additionalLibs: Set[String] = Set()) {

  val log = Logger.getLogger(getClass.getName)
  val timeout = 60.seconds

  val baseLibs = Seq(
    s"scala3-library_3-${Config.scalaVersion}.jar",
     s"scala-library-${Config.scala2LibraryVersion}.jar",
    s"scalajs-library_2.13-${Config.scalaJSVersion}.jar",
    s"scalajs-javalib-${Config.scalaJSVersion}.jar"
    )

  val repoSJSRE = """([^ %]+) *%%% *([^ %]+) *% *([^ %]+)""".r
  val repoRE = """([^ %]+) *%% *([^ %]+) *% *([^ %]+)""".r
  val repoBase = "https://repo1.maven.org/maven2"
  val sjsVersion = s"_sjs${Config.scalaJSMainVersion}_${Config.scalaMainVersion}"

  val commonLibraries = {
    log.info("Loading files...")
    // load all external libs in parallel using spray-client
    val jarFiles = (additionalLibs.toSeq ++ baseLibs).map { name => // do in parallel
      val stream = context.getResourceAsStream(relativeJarPath + name)
      log.info(s"Loading resource $name")
      if (stream == null) {
        throw new Exception(s"Classpath loading failed, jar $name not with relative JAR path '$relativeJarPath'")
      }
      name -> Streamable.bytes(stream)
    }.seq

    log.info("Files loaded...")

    jarFiles
  }
  
  val virtualSet = commonLibraries.map {
    case (name, data) =>
      lib4compiler(name, data)
  }

  /**
   * The loaded files shaped for Scalac to use
   */
  def lib4compiler(name: String, bytes: Array[Byte]) = {
    log.info(s"Loading $name for Scalac")
    val in = new ZipInputStream(new ByteArrayInputStream(bytes))
    val entries = Iterator
      .continually({
        try {
          in.getNextEntry
        } catch {
          case e: Exception =>
            null
        }
      })
      .takeWhile(_ != null)
      .map(x => {
        (x, Streamable.bytes(in))
      })

    val dir = new MyVirtualDirectory(name, None)
    for {
      (e, data) <- entries
      if !e.isDirectory
    } {
      val tokens = e.getName.split("/")
      var d = dir
      for (t <- tokens.dropRight(1)) {
        d = d.subdirectoryNamed(t).asInstanceOf[MyVirtualDirectory]
      }
      val f = d.fileNamed(tokens.last)
      val o = f.bufferedOutput
      o.write(data)
      o.close()
    }
    dir
  }

  /**
   * The loaded files shaped for Scala-Js-Tools to use
   */
  def lib4linker(name: String, bytes: Array[Byte]) = {
    val in = new ZipInputStream(new ByteArrayInputStream(bytes))
    val entries = Iterator
      .continually({
        try {
          in.getNextEntry
        } catch {
          case e: Exception =>
            null
        }
      })
      .takeWhile(_ != null)
      .map(x => {
        (x, Streamable.bytes(in))
      })

    val files = mutable.ArrayBuffer[MemIRFileImpl]()
    for {
      (e, data) <- entries
      if !e.isDirectory
    } {
      val path = e.getName
      if (path.endsWith(".sjsir")) files.addOne(new MemIRFileImpl(path, None, data))
    }
    MemJarIRContainer(name, files.toList)
  }

  class MemJarIRContainer(path: String, files: List[MemIRFileImpl])
      extends IRContainerImpl(path, None) {
    def sjsirFiles(implicit ec: ExecutionContext): Future[List[IRFile]] = Future.successful(files)  
  }

  /**
   * In memory cache of all the jars used in the compiler. This takes up some
   * memory but is better than reaching all over the filesystem every time we
   * want to do something.
   */
  val commonLibraries4compiler = virtualSet

  /**
   * In memory cache of all the jars used in the linker.
   */
  val commonLibraries4linker = commonLibraries.map { case (name, data) => lib4linker(name, data) }

  val linkerCaches = mutable.Map.empty[List[String], List[IRFile]]

  def compilerLibraries(extLibs: List[String]) = commonLibraries4compiler

  def linkerLibraries(extLibs: List[String]) = {
    linkerCaches.getOrElseUpdate(extLibs, {
      val loadedJars = commonLibraries4linker
      val cache = StandardImpl.irFileCache().newCache
      val res = Await.result(cache.cached(loadedJars)(ExecutionContext.global), Duration.Inf)
      log.fine("Loaded scalaJSClassPath")
      res.toList
    })
  }
}
