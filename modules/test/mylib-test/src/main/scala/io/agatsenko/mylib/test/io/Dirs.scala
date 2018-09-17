/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-17
  */
package io.agatsenko.mylib.test.io

import java.io.IOException
import java.nio.file.{Files, FileVisitResult, Path, SimpleFileVisitor}
import java.nio.file.attribute.BasicFileAttributes

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
}
