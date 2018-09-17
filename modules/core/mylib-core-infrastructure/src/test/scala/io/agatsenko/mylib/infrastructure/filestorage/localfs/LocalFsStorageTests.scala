/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-17
  */
package io.agatsenko.mylib.infrastructure.filestorage.localfs

import scala.util.Try

import java.io.{ByteArrayInputStream, InputStream}
import java.net.URI
import java.nio.file.{Files, Path}
import java.nio.file.StandardOpenOption._

import io.agatsenko.mylib.test.io.Dirs
import io.agatsenko.mylib.test.util.Generator.{newStr, newUuidStr}
import io.mango.common.resource.using
import io.mango.common.util.TryExt
import org.scalatest.{FunSuite, Matchers}

class LocalFsStorageTests extends FunSuite with Matchers {
  import LocalFsStorageTests._

  test("files should return iterator of all files in storage") {
    testWithStorage { implicit storage =>
      val expectedFilePaths = Seq(
        newFileInStorage(),
        newFileInStorage(),
        newFileInStorage()
      )

      val actualFilePaths = storage.filesIterator.toSeq.map(_.path.fullNativePath)
      assert(actualFilePaths.size == expectedFilePaths.size)
      assert(actualFilePaths.forall(expectedFilePaths.contains))
    }
  }

  test("exists(path) should return true if file exists in storage by specified path") {
    testWithStorage { implicit storage =>
      val fileNativePath = newFileInStorage()
      assert(storage.exists(toStoragePath(fileNativePath)))
    }
  }

  test("exists(path) should return false if file does exist in storage by specified path") {
    testWithStorage { implicit storage =>
      val path = toStoragePath(storage.newFileNativePath(newUuidStr))
      assert(!storage.exists(path))
    }
  }

  test("exists(file) should return true if file exists in storage") {
    testWithStorage { implicit storage =>
      val fileNativePath = newFileInStorage()
      val file = storage.get(toStoragePath(fileNativePath)).get
      assert(storage.exists(file))
    }
  }

  test("exists(file) should return false if file does exist in storage") {
    testWithStorage { implicit storage =>
      val nativePath = newFileInStorage()
      val file = storage.get(toStoragePath(nativePath)).get
      Dirs.del(nativePath.getParent)
      assert(!storage.exists(file))
    }
  }

  test("exists(uri) should return true if file exists in storage by specified uri") {
    // FIXME: not yet implemented
    ???
  }

  test("exists(uri) should return false if file does exist in storage by specified uri") {
    // FIXME: not yet implemented
    ???
  }

  test("exists(uriStr) should return true if file exists in storage by specified uriStr") {
    // FIXME: not yet implemented
    ???
  }

  test("exists(uriStr) should return false if file does exist in storage by specified uriStr") {
    // FIXME: not yet implemented
    ???
  }

  def openStorage(): LocalFsStorage = LocalFsStorage.open(Dirs.newTmpDir())

  def testWithStorage(test: LocalFsStorage => Unit): Unit = {
    val storage = openStorage()
    Try {
      using(storage)(test(_))
    }.eventually(_ => Dirs.del(storage.rootDir)).get
  }

  def toStoragePath(path: Path)(implicit storage: LocalFsStorage): storage.FsPath = {
    val relativePath = if (path.isAbsolute) storage.rootDir.relativize(path) else path
    storage.toPath(
      new URI(LocalFsStorage.URI_SCHEMA, relativePath.getParent.toString, "/" + relativePath.getFileName.toString, null)
    )
  }

  def newFileInputStream(): InputStream = {
    val sb = StringBuilder.newBuilder
    for (_ <- 1 to NEW_FILE_STR_COUNT) {
      sb.append(newStr(NEW_FILE_STR_LENGTH)).append("\n")
    }
    new ByteArrayInputStream(sb.toString().getBytes)
  }

  def newFileInStorage()(implicit storage: LocalFsStorage): Path = {
    val file = storage.rootDir.resolve(storage.newFileNativePath(newUuidStr))
    Files.createDirectory(file.getParent)
    using(newFileInputStream()) { in =>
      using(Files.newOutputStream(file, WRITE, CREATE_NEW)) { out =>
        var b = -1
        while({b = in.read(); b > -1}) {
          out.write(b)
        }
        out.flush()
      }
    }
    file
  }

  def putNewFileIntoStorage()(implicit storage: LocalFsStorage): storage.FsFile = {
    using(newFileInputStream()) { in =>
      storage.putNew(newUuidStr, in)
    }
  }
}

object LocalFsStorageTests {
  val NEW_FILE_STR_LENGTH = 50
  val NEW_FILE_STR_COUNT = 20
}
