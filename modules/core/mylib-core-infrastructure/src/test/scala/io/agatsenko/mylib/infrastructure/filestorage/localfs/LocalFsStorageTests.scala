/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-17
  */
package io.agatsenko.mylib.infrastructure.filestorage.localfs

import scala.util.Try

import java.io.{ByteArrayInputStream, InputStream}
import java.net.URI
import java.nio.charset.StandardCharsets
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
    testWithStorage { implicit storage =>
      val fileNativePath = newFileInStorage()
      assert(storage.exists(toStoragePath(fileNativePath).uri))
    }
  }

  test("exists(uri) should return false if file does exist in storage by specified uri") {
    testWithStorage { implicit storage =>
      val uri = toStoragePath(storage.newFileNativePath(newUuidStr)).uri
      assert(!storage.exists(uri))
    }
  }

  test("exists(uriStr) should return true if file exists in storage by specified uriStr") {
    testWithStorage { implicit storage =>
      val fileNativePath = newFileInStorage()
      assert(storage.exists(toStoragePath(fileNativePath).uri.toString))
    }
  }

  test("exists(uriStr) should return false if file does exist in storage by specified uriStr") {
    testWithStorage { implicit storage =>
      val uriStr = toStoragePath(storage.newFileNativePath(newUuidStr)).uri.toString
      assert(!storage.exists(uriStr))
    }
  }

  test("get(path) should return Some(file) if file exists in storage by specified path") {
    testWithStorage { implicit storage =>
      val path = toStoragePath(newFileInStorage())
      val file = storage.get(path)
      assert(file != null)
      assert(file.isDefined)
      assert(file.get.path.fullNativePath == path.fullNativePath)
    }
  }

  test("get(path) should return None if file dose not exist in storage by specified path") {
    testWithStorage { implicit storage =>
      val path = toStoragePath(storage.newFileNativePath(newUuidStr))
      val file = storage.get(path)
      assert(file != null)
      assert(file.isEmpty)
    }
  }

  test("get(uri) should return Some(file) if file exists in storage by specified uri") {
    testWithStorage { implicit storage =>
      val path = toStoragePath(newFileInStorage())
      val uri = path.uri
      val file = storage.get(uri)
      assert(file != null)
      assert(file.isDefined)
      assert(file.get.path.fullNativePath == path.fullNativePath)
    }
  }

  test("get(uri) should return None if file dose not exist in storage by specified uri") {
    testWithStorage { implicit storage =>
      val path = toStoragePath(storage.newFileNativePath(newUuidStr))
      val uri = path.uri
      val file = storage.get(uri)
      assert(file != null)
      assert(file.isEmpty)
    }
  }

  test("get(uriStr) should return Some(file) if file exists in storage by specified uriStr") {
    testWithStorage { implicit storage =>
      val path = toStoragePath(newFileInStorage())
      val uriStr = path.uri.toString
      val file = storage.get(uriStr)
      assert(file != null)
      assert(file.isDefined)
      assert(file.get.path.fullNativePath == path.fullNativePath)
    }
  }

  test("get(uriStr) should return None if file dose not exist in storage by specified uriStr") {
    testWithStorage { implicit storage =>
      val path = toStoragePath(storage.newFileNativePath(newUuidStr))
      val uriStr = path.uri.toString
      val file = storage.get(uriStr)
      assert(file != null)
      assert(file.isEmpty)
    }
  }

  test("map(f) should apply f for all existing files") {
    testWithStorage { implicit storage =>
      val expectedPaths = Seq(
        newFileInStorage(),
        newFileInStorage(),
        newFileInStorage()
      )

      val actualPaths: Seq[Path] = storage.map(f => f.path.fullNativePath)
      assert(actualPaths.size == expectedPaths.size)
      assert(actualPaths.forall(expectedPaths.contains))
    }
  }

  test("filter(f) should apply f for all existing files") {
    testWithStorage { implicit storage =>
      val expected = Seq(
        newFileInStorage(),
        newFileInStorage(),
        newFileInStorage(),
        newFileInStorage()
      ).zipWithIndex.filter(t => ((t._2 + 1) & 1) == 0).map(_._1)

      val actual = storage.filter[Seq](f => expected.contains(f.path.fullNativePath)).map(_.path.fullNativePath)
      assert(actual.size == expected.size)
      assert(actual.forall(expected.contains))
    }
  }

  test("foreach(f) should apply f for all existing files") {
    testWithStorage { implicit storage =>
      val expected = Seq(
        newFileInStorage(),
        newFileInStorage(),
        newFileInStorage()
      )

      val actualBuilder = Seq.newBuilder[Path]
      storage.foreach(actualBuilder += _.path.fullNativePath)
      val actual = actualBuilder.result()
      assert(actual.size == expected.size)
      assert(actual.forall(expected.contains))
    }
  }

  test("putNew(fileName, inputStream) should put new file to storage") {
    testWithStorage { implicit storage =>
      val fileName = newUuidStr
      val content = newFileContent
      val file = using(newFileInputStream(content))(storage.putNew(fileName, _))
      assert(file != null)
      assert(file.name == fileName)
      assert(file.path.fullNativePath.getFileName.toString == fileName)
      assert(Dirs.getContentString(file.path.fullNativePath, defaultCharset) == content)
    }
  }

  test("put(path, inputStream) should update file if file exists in storage by specified path") {
    // FIXME: not yet implemented
    ???
  }

  test("put(path, inputStream) should throw IllegalStateException if file does not exist in storage by specified path") {
    // FIXME: not yet implemented
    ???
  }

  test("put(file, inputStream) should update file if file exists in storage by specified path") {
    // FIXME: not yet implemented
    ???
  }

  test("put(file, inputStream) should throw IllegalStateException if file does not exist in storage") {
    // FIXME: not yet implemented
    ???
  }

  test("put(uri, inputStream) should update file if file exists in storage by specified uri") {
    // FIXME: not yet implemented
    ???
  }

  test("put(uri, inputStream) should throw IllegalStateException if file does not exist in storage by specified uri") {
    // FIXME: not yet implemented
    ???
  }

  test("put(uriStr, inputStream) should update file if file exists in storage by specified uriStr") {
    // FIXME: not yet implemented
    ???
  }

  test("put(uriStr, inputStream) should throw IllegalStateException if file does not exist in storage by specified uriStr") {
    // FIXME: not yet implemented
    ???
  }

  test("remove(path) should remove file from storage and return true if file exists in storage by specified path") {
    // FIXME: not yet implemented
    ???
  }

  test("remove(path) should return false if file does not exist in storage by specified path") {
    // FIXME: not yet implemented
    ???
  }

  test("remove(file) should remove file from storage and return true if file exists in storage") {
    // FIXME: not yet implemented
    ???
  }

  test("remove(file) should return false if file does not exist in storage") {
    // FIXME: not yet implemented
    ???
  }

  test("remove(uri) should remove file from storage and return true if file exists in storage by specified uri") {
    // FIXME: not yet implemented
    ???
  }

  test("remove(uri) should return false if file does not exist in storage by specified uri") {
    // FIXME: not yet implemented
    ???
  }

  test("remove(uriStr) should remove file from storage and return true if file exists in storage by specified uriStr") {
    // FIXME: not yet implemented
    ???
  }

  test("remove(uriStr) should return false if file does not exist in storage by specified uriStr") {
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

  def newFileContent: String = {
    val sb = StringBuilder.newBuilder
    for (_ <- 1 to NEW_FILE_STR_COUNT) {
      sb.append(newStr(NEW_FILE_STR_LENGTH)).append("\n")
    }
    sb.toString()
  }

  def newFileInputStream(content: String): InputStream = new ByteArrayInputStream(content.getBytes(defaultCharset))

  def newFileInputStream: InputStream = newFileInputStream(newFileContent)

  def newFileInStorage()(implicit storage: LocalFsStorage): Path = {
    val file = storage.rootDir.resolve(storage.newFileNativePath(newUuidStr))
    Files.createDirectory(file.getParent)
    using(newFileInputStream) { in =>
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
    using(newFileInputStream) { in =>
      storage.putNew(newUuidStr, in)
    }
  }
}

object LocalFsStorageTests {
  val NEW_FILE_STR_LENGTH = 3
  val NEW_FILE_STR_COUNT = 2

  val defaultCharset = StandardCharsets.UTF_8
}
