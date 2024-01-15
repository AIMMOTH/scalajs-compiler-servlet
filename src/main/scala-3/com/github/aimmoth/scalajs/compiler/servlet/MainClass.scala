package com.aimmoth.scalajs.compiler.servlet

import org.objectweb.asm
import dotty.tools.io.AbstractFile

// Adapted from: 
// https://github.com/VirtusLab/scala-cli/blob/acb2d751842bebcc19263af4bf55fd76065aa191/modules/build/src/main/scala/scala/build/internal/MainClass.scala
object MainClass {

  private def stringArrayDescriptor = "([Ljava/lang/String;)V"

  private class MainMethodChecker extends asm.ClassVisitor(asm.Opcodes.ASM9) {
    private var foundMainClass = false
    private var nameOpt        = Option.empty[String]
    def found: Boolean         = foundMainClass
    override def visit(
      version: Int,
      access: Int,
      name: String,
      signature: String,
      superName: String,
      interfaces: Array[String]
    ): Unit = {
      nameOpt = Some(name.replace('/', '.').replace('\\', '.'))
    }
    override def visitMethod(
      access: Int,
      name: String,
      descriptor: String,
      signature: String,
      exceptions: Array[String]
    ): asm.MethodVisitor = {
      def isStatic = (access & asm.Opcodes.ACC_STATIC) != 0
      if (name == "main" && descriptor == stringArrayDescriptor && isStatic)
        foundMainClass = true
      null
    }
    def mainClassOpt: Option[String] =
      if (foundMainClass) nameOpt else None
  }
  def find(output: List[AbstractFile]): Seq[String] =
    output.flatMap{ file =>
      val is = file.input
      val reader  = new asm.ClassReader(is)
      val checker = new MainMethodChecker
      reader.accept(checker, 0)
      checker.mainClassOpt.iterator
    }.toSeq
}
