/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-17
  */
package io.agatsenko.mylib.test.io

import java.io.{BufferedInputStream, IOException}
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.StandardOpenOption._

import io.mango.common.resource.using

object Dirs {
  def newTmpDir(): Path = Files.createTempDirectory(null)

  def del(path: Path): Unit = {
    val absolutePath = path.toAbsolutePath
    if (Files.exists(absolutePath)) {
      Files.walkFileTree(
        absolutePath,
        new SimpleFileVisitor[Path] {
          override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
            Files.delete(file)
            FileVisitResult.CONTINUE
          }
          override def postVisitDirectory(dir: Path, ex: IOException): FileVisitResult = {
            if (ex != null) {
              throw ex
            }
            Files.delete(dir)
            FileVisitResult.CONTINUE
          }
        }
      )
    }
  }

  def getContentString(file: Path, charset: Charset = StandardCharsets.UTF_8): String = {
    def append(sb: StringBuilder, bytes: Array[Byte], count: Int): Unit = {
      if (count > 0) {
        sb.append(new String(bytes, 0, count, charset))
      }
    }

    val sb = StringBuilder.newBuilder
    val bytes = new Array[Byte](256)
    using(new BufferedInputStream(Files.newInputStream(file, READ))) { in =>
      var b = -1
      var readCount = 0
      while ({b = in.read(); b > -1}) {
        if (readCount >= bytes.length) {
          append(sb, bytes, readCount)
          readCount = 0
        }
        bytes(readCount) = b.toByte
        readCount += 1
      }
      append(sb, bytes, readCount)
    }
    sb.toString()
  }
}
