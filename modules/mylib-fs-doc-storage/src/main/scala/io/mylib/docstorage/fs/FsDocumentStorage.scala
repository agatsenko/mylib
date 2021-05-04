/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2019-09-25
 */
package io.mylib.docstorage.fs

import scala.util.Try

import java.io.{InputStream, IOException}
import java.net.URI
import java.nio.file.{Files, FileVisitResult, Path, Paths, SimpleFileVisitor}
import java.nio.file.StandardOpenOption.{CREATE_NEW, WRITE}
import java.nio.file.attribute.BasicFileAttributes
import java.util.UUID

import io.mango.common.io.Streams
import io.mango.common.resource
import io.mango.common.util.{Check, TryExt}
import io.mylib.docstorage.{DocumentStorage, DocumentStorageException}

class FsDocumentStorage private(val rootDir: Path) extends DocumentStorage {
  import FsDocumentStorage._

  override type TStorage = FsDocumentStorage
  override type TPath = FsDocumentPath
  override type TDocument = FsDocument
  override type TDocumentIterator = FsDocumentIterator

  Check.argNotNull(rootDir, "rootDir")
  Check.arg(rootDir.isAbsolute, "rootDir is not an absolute")
  Check.arg(Files.exists(rootDir), s"path '$rootDir' does not exist")
  Check.arg(Files.isDirectory(rootDir), s"path '$rootDir' is not directory")

  private var closed = false

  override def toUri(uriStr: String): URI = {
    checkIsOpen()
    Check.argNotEmpty(uriStr, "uriStr")
    val uri = Try(new URI(uriStr))
        .convertFailure[DocumentStorageException](
          new DocumentStorageException(s"unable to convert '$uriStr' to URI", _)
        )
        .get
    checkUri(uri)
    uri
  }

  override def toPath(uri: URI): FsDocumentPath = {
    checkIsOpen()
    checkUri(uri)
    FsDocumentPath(uri, rootDir.resolve(uri.getPath))
  }

  override def docsIterator: FsDocumentIterator = {
    checkIsOpen()
    new FsDocumentIterator()
  }

  override def exists(path: FsDocumentPath): Boolean = {
    checkIsOpen()
    Check.argNotNull(path, "path")
    Files.exists(rootDir.resolve(path.nativeFullPath))
  }

  override def get(path: FsDocumentPath): Option[FsDocument] = {
    checkIsOpen()
    if (exists(path)) Some(FsDocument(path)) else None
  }

  override def putNew(docName: String, in: InputStream): FsDocument = {
    checkIsOpen()
    Check.argNotEmpty(docName, "docName")
    Check.argNotNull(in, "in")
    val nativeRelativePath = createNewNativeRelativePath(docName)
    val path = FsDocumentPath(createUri(nativeRelativePath), rootDir.resolve(nativeRelativePath))
    putDocument(path, in)
  }

  override def put(path: FsDocumentPath, in: InputStream): FsDocument = {
    checkIsOpen()
    Check.argNotNull(path, "path")
    Check.argNotNull(in, "in")
    if (exists(path)) {
      remove(path)
    }
    putDocument(path, in)
  }

  override def remove(path: FsDocumentPath): Boolean = {
    checkIsOpen()
    if (exists(path)) {
      Files.walkFileTree(
        path.nativeFullPath.getParent,
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
      true
    }
    else {
      false
    }
  }

  override def isClosed: Boolean = closed

  override def close(): Unit = {
    closed = true
  }

  override def toString: String = s"${getClass.getSimpleName}($rootDir)"

  private def checkIsOpen(): Unit = {
    if (isClosed) {
      throw new DocumentStorageException("unable to perform the operation because the storage is closed")
    }
  }

  private def checkUri(uri: URI): Unit = {
    Check.argNotNull(uri, "uri")
    Check.arg(uri.getScheme == URI_SCHEMA, "uriStr contains invalid schema")
    Check.arg(uri.getHost != null, "uriStr does not contain host")
    Check.arg(uri.getPort == -1, "uriStr contains port")
    Check.arg(uri.getRawQuery == null, "uriStr contains query")
    Check.arg(uri.getRawFragment == null, "uriStr contains fragment")
    Check.arg(uri.getPath != null, "uriStr does not contain path")
  }

  private def checkNativeRelativePath(path: Path): Unit = {
    Check.argNotNull(path, "path")
    Check.arg(!path.isAbsolute, "path is absolute")
    Check.arg(path.getNameCount == NATIVE_PATH_NAME_COUNT, "path name count is invalid")
  }

  private def createUri(nativeRelativePath: Path): URI = {
    checkNativeRelativePath(nativeRelativePath)
    new URI(URI_SCHEMA, nativeRelativePath.getParent.toString, "/" + nativeRelativePath.getFileName.toString, null)
  }

  private def putDocument(path: FsDocumentPath, in: InputStream): FsDocument = {
    Files.createDirectory(path.nativeFullPath.getParent)
    Try {
      resource.using(Files.newOutputStream(path.nativeFullPath, WRITE, CREATE_NEW)) { out =>
        Streams.copy(in, out)
      }
      FsDocument(path)
    }.convertFailure(new DocumentStorageException(s"unable to put '$path' document", _)).get
  }

  private def createNewNativeRelativePath(docName: String): Path =
    Paths.get(UUID.randomUUID().toString.replace("-", ""), docName)

  case class FsDocumentPath private[FsDocumentStorage](uri: URI, nativeFullPath: Path) extends DocumentPath {
    override def docName: String = nativeFullPath.getFileName.toString

    override def storage: FsDocumentStorage = FsDocumentStorage.this

    override def toString: String = s"${getClass.getSimpleName}($uri)"
  }

  object FsDocumentPath {
    private[FsDocumentStorage] def apply(uri: URI, nativeFullPath: Path): FsDocumentPath =
      new FsDocumentPath(uri, nativeFullPath)
  }

  case class FsDocument private[FsDocumentStorage](path: FsDocumentPath) extends Document {
    override def inputStream(): InputStream = {
      // FIXME: not yet implemented
      ???
    }

    override def storage: FsDocumentStorage = {
      // FIXME: not yet implemented
      ???
    }

    override def toString: String = s"${getClass.getSimpleName}(${path.uri})"
  }

  class FsDocumentIterator private[FsDocumentStorage]() extends DocumentIterator {
    override def isClosed: Boolean = {
      // FIXME: not yet implemented
      ???
    }

    override def hasNext: Boolean = {
      // FIXME: not yet implemented
      ???
    }

    override def next(): FsDocument = {
      // FIXME: not yet implemented
      ???
    }

    override def close(): Unit = {
      // FIXME: not yet implemented
      ???
    }
  }
}

object FsDocumentStorage {
  private val URI_SCHEMA = "fs"
  private val NATIVE_PATH_NAME_COUNT = 2

  def apply(rootDir: Path): FsDocumentStorage = {
    // FIXME: not yet implemented
    ???
  }
}

