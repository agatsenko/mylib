/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-17
  */
package io.agatsenko.mylib.test.io

import java.io.BufferedOutputStream
import java.nio.file.{Files, Path, Paths}
import java.nio.file.StandardOpenOption._

import io.agatsenko.mylib.test.util.Generator.{newStr, newUuidStr}
import io.mango.common.resource.using
import org.scalatest.{FunSuite, Matchers}

class DirsTests extends FunSuite with Matchers {
  import DirsTests._

  test("newTmpDir() should create new dir in temporary path") {
    val dirPath = Dirs.newTmpDir()
    assert(dirPath != null)
    assert(Files.exists(dirPath))
    assert(dirPath != rootTmpDirPath)
    assert(dirPath.startsWith(rootTmpDirPath))
    Files.delete(dirPath)
  }

  test("del(dir) should delete dir and its sub-items") {
    val dir = Dirs.newTmpDir()
    val subDir = dir.resolve(newUuidStr)
    Files.createDirectory(subDir)
    generateFile(dir)
    generateFile(subDir)

    Dirs.del(dir)
    assert(!Files.exists(dir))
  }

  test("del(file) should delete file") {
    val dir = Dirs.newTmpDir()
    val file = generateFile(dir)

    Dirs.del(file)
    assert(!Files.exists(file))

    Files.delete(dir)
  }
}

object DirsTests {
  val NEW_FILE_STRS = 10

  val rootTmpDirPath: Path = Paths.get(System.getProperty("java.io.tmpdir"))

  def generateFile(dir: Path): Path = {
    val file = dir.resolve(newUuidStr).toAbsolutePath
    using(new BufferedOutputStream(Files.newOutputStream(file, WRITE, CREATE_NEW))) { out =>
      for (_ <- 1 to NEW_FILE_STRS) {
        out.write(newStr(50).getBytes)
      }
      out.flush()
    }
    file
  }
}