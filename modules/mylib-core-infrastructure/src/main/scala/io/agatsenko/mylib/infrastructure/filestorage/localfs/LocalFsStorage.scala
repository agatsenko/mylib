/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-16
  */
package io.agatsenko.mylib.infrastructure.filestorage.localfs

import java.io.{InputStream, IOException}
import java.net.URI
import java.nio.file._
import java.nio.file.StandardOpenOption._
import java.nio.file.attribute.BasicFileAttributes
import java.util.UUID

import io.agatsenko.mylib.infrastructure.filestorage.DocStorage
import io.agatsenko.mylib.infrastructure.io.Streams
import io.mango.common.resource.using
import io.mango.common.util.Check

// FIXME: need to remove
class LocalFsStorage private(val rootDir: Path) extends DocStorage {
  import LocalFsStorage._

  override type TStorage = LocalFsStorage
  override type TPath = FsPath
  override type TDoc = FsFile

  private var closed = false

  Check.argNotNull(rootDir, "rootDir")
  Check.arg(rootDir.isAbsolute, s"rootDir ($rootDir) is not absolute")
  Check.arg(Files.exists(rootDir), s"rootDir ($rootDir) is not exist")
  Check.arg(Files.isDirectory(rootDir), s"rootDir ($rootDir) is not directory")

  override def isClosed: Boolean = closed

  override def close(): Unit = {
    closed = true
  }

  def newFileNativePath(fileName: String): Path = Paths.get(UUID.randomUUID().toString.replace("-", ""), fileName)

  override def toUri(uriStr: String): URI = {
    checkIsOpened()
    Check.argNotEmpty(uriStr, "uriStr")

    val uri = new URI(uriStr)
    checkUri(uri)
    uri
  }

  override def toPath(uri: URI): FsPath = {
    checkIsOpened()
    new FsPath(uri)
  }

  override def filesIterator: StorageDocIterator = {
    checkIsOpened()
    new FsFileIterator
  }

  override def exists(path: FsPath): Boolean = {
    checkIsOpened()
    Files.exists(path.fullNativePath) && !Files.isDirectory(path.fullNativePath)
  }

  override def get(path: FsPath): Option[FsFile] = {
    checkIsOpened()
    if (exists(path)) Some(FsFile(path)) else None
  }

  override def putNew(fileName: String, in: InputStream): FsFile = {
    checkIsOpened()
    val filePath = new FsPath(newFileNativePath(fileName))
    Files.createDirectory(filePath.fullNativePath.getParent)
    using(Files.newOutputStream(filePath.fullNativePath, WRITE, CREATE_NEW)) { out =>
      Streams.copy(in, out)
    }
    FsFile(filePath)
  }

  override def put(path: FsPath, in: InputStream): TDoc = {
    import io.mango.common.util.OptionEx

    checkIsOpened()
    get(path).map { file =>
      using(Files.newOutputStream(file.path.fullNativePath, WRITE, TRUNCATE_EXISTING)) { out =>
        Streams.copy(in, out)
      }
      file
    }.getOrThrow(new IllegalStateException(s"'${path.uri}' file is not found"))
  }

  override def remove(path: FsPath): Boolean = {
    checkIsOpened()
    if (exists(path)) {
      Files.walkFileTree(
        path.fullNativePath.getParent,
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

  private def checkIsOpened(): Unit = {
    Check.state(isOpen, "unable to perform operation because the storage is closed")
  }

  case class FsPath private[LocalFsStorage](override val uri: URI, fullNativePath: Path) extends StorageDocPath {
    def this(uri: URI) {
      this(uri, rootDir.resolve(resolveNativePath(uri)))
    }

    def this(nativePath: Path) {
      this(resolveUri(nativePath), rootDir.resolve(nativePath))
    }

    override val fileName: String = fullNativePath.getFileName.toString

    override def storage: LocalFsStorage = LocalFsStorage.this

    override def toString: String = s"${getClass.getSimpleName}($uri)"
  }

  case class FsFile private[LocalFsStorage](path: FsPath) extends Document {
    override def storage: LocalFsStorage = LocalFsStorage.this

    override def inputStream: InputStream = {
      checkIsOpened()
      Files.newInputStream(path.fullNativePath, READ)
    }

    override def toString: String = s"${getClass.getSimpleName}(${path.uri})"
  }

  private class FsFileIterator extends StorageDocIterator {
    LocalFsStorage.this.checkIsOpened()

    private val pathStream = Files.find(
      rootDir,
      2,
      (p: Path, a: BasicFileAttributes) => {
        !a.isDirectory &&
        p.startsWith(rootDir) &&
        p != rootDir &&
        rootDir.relativize(p).getNameCount == NATIVE_PATH_NAME_COUNT
      },
      FileVisitOption.FOLLOW_LINKS
    )

    private val pathIter = pathStream.iterator()

    private var closed = false

    override def isClosed: Boolean = LocalFsStorage.this.isClosed || closed

    override def close(): Unit = {
      if (!closed) {
        try {
          pathStream.close()
        }
        finally {
          closed = true
        }
      }
    }

    override def hasNext: Boolean = {
      checkIsOpened()
      pathIter.hasNext
    }

    override def next(): FsFile = {
      checkIsOpened()
      FsFile(new FsPath(rootDir.relativize(pathIter.next())))
    }

    private def checkIsOpened(): Unit = {
      Check.state(!isClosed, "unable to perform operation because the iterator is closed")
    }
  }
}

object LocalFsStorage {
  private[localfs] val URI_SCHEMA = "localfs"
  private[localfs] val NATIVE_PATH_NAME_COUNT = 2

  def open(rootDir: Path): LocalFsStorage = {
    Check.argNotNull(rootDir, "rootDir")
    new LocalFsStorage(rootDir.toAbsolutePath)
  }

  def open(rootDir: String): DocStorage = open(Paths.get(rootDir))

  private def checkUri(uri: URI): Unit = {
    Check.argNotNull(uri, "uri")
    Check.arg(uri.getScheme == URI_SCHEMA, "uriStr contains invalid schema")
    Check.arg(uri.getHost != null, "uriStr does not contain host")
    Check.arg(uri.getPort == -1, "uriStr contains port")
    Check.arg(uri.getRawQuery == null, "uriStr contains query")
    Check.arg(uri.getRawFragment == null, "uriStr contains fragment")
    Check.arg(uri.getPath != null, "uriStr does not contain path")
  }

  private def checkNativePath(path: Path): Unit = {
    Check.argNotNull(path, "path")
    Check.arg(!path.isAbsolute, "path is absolute")
    Check.arg(path.getNameCount == NATIVE_PATH_NAME_COUNT, "path name count is invalid")
  }

  private def resolveNativePath(uri: URI): Path = {
    checkUri(uri)

    val path = Paths.get(uri.getHost, uri.getPath)
    Check.arg(path.getNameCount == NATIVE_PATH_NAME_COUNT, "uri contains invalid path")
    path
  }

  private def resolveUri(path: Path): URI = {
    checkNativePath(path)
    new URI(URI_SCHEMA, path.getParent.toString, "/" + path.getFileName.toString, null)
  }
}
