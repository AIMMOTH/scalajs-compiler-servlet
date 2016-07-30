package fiddle

import scala.language.postfixOps
import scala.reflect.io.VirtualFile

import org.slf4j.LoggerFactory

class ScalaJsCompiler {

  val log = LoggerFactory.getLogger(getClass)

  def compileScalaJsString(context : ClassLoader, source: String, optimizer: Optimizer, relativeJarPath: String, additionalLibs : List[String] = Nil): String = {
    compileScalaJsStrings(context, List(source), optimizer, relativeJarPath, additionalLibs)
  }
  
  /**
   * String with Scala JS code
   */
  def compileScalaJsStrings(context : ClassLoader, sources: List[String], optimizer: Optimizer, relativeJarPath: String, additionalLibs : List[String] = Nil): String = {
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
      case cr =>
        throw new Exception(cr.log)
    }
  }

}