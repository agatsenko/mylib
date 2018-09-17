/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-16
  */
package io.agatsenko.mylib.infrastructure.filestorage.localfs

import java.io.{InputStream, IOException}
import java.nio.file._
import java.nio.file.StandardOpenOption._
import java.nio.file.attribute.BasicFileAttributes
import java.util.UUID

import io.agatsenko.mylib.infrastructure.filestorage.FileStorage
import io.agatsenko.mylib.infrastructure.io.Streams
import io.mango.common.resource.using
import io.mango.common.util.Check

class LocalFsStorage private(val rootDir: Path) extends FileStorage {
  override type TStorage = LocalFsStorage
  override type TPath = FsPath
  override type TFile = FsFile

  Check.argNotNull(rootDir, "rootDir")
  Check.arg(rootDir.isAbsolute, s"rootDir ($rootDir) is not absolute")
  Check.arg(Files.exists(rootDir), s"rootDir ($rootDir) is not exist")
  Check.arg(Files.isDirectory(rootDir), s"rootDir ($rootDir) is not directory")

  override def toPath(path: String, morePaths: String*): FsPath = {
    FsPath(rootDir.resolve(Paths.get(path, morePaths: _*)))
  }

  override def files: StorageFileIterator = new FsFileIterator

  override def exists(path: FsPath): Boolean = {
    Files.exists(path.underlyingPath) &&
    !Files.isDirectory(path.underlyingPath) &&
    path.underlyingPath.startsWith(rootDir) &&
    path.underlyingPath.getParent != rootDir &&
    path.underlyingPath.getParent.getParent == rootDir
  }

  override def get(path: FsPath): Option[FsFile] = if (exists(path)) Some(FsFile(path)) else None

  override def putNew(fileName: String, in: InputStream): FsFile = {
    val fileDirPath = rootDir.resolve(UUID.randomUUID().toString.replace("-", ""))
    val filePath = fileDirPath.resolve(fileName)
    Files.createDirectory(fileDirPath)
    using(Files.newOutputStream(filePath, WRITE, CREATE_NEW)) { out =>
      Streams.copy(in, out)
    }
    FsFile(FsPath(filePath))
  }

  override def put(path: FsPath, in: InputStream): TFile = {
    import io.mango.common.util.OptionEx

    get(path).map { file =>
      using(Files.newOutputStream(file.path.underlyingPath, WRITE, TRUNCATE_EXISTING)) { out =>
        Streams.copy(in, out)
      }
      file
    }.getOrThrow(new IllegalStateException(s"'${path.underlyingPath}' file is not found"))
  }

  override def remove(path: FsPath): Boolean = {
    if (exists(path)) {
      Files.walkFileTree(
        path.underlyingPath.getParent,
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

  case class FsPath private(underlyingPath: Path) extends StorageFilePath {
    val fileName: String = underlyingPath.getFileName.toString

    override def storage: LocalFsStorage = LocalFsStorage.this
  }

  case class FsFile private(path: FsPath) extends StorageFile {
    override def storage: LocalFsStorage = LocalFsStorage.this

    override def toString: String = s"${getClass.getSimpleName}(${path.underlyingPath})"

    override def inputStream: InputStream = Files.newInputStream(path.underlyingPath, READ)
  }

  private class FsFileIterator extends StorageFileIterator {
    private val pathStream = Files.find(
      rootDir,
      2,
      (p: Path, a: BasicFileAttributes) => {
        !a.isDirectory && p.startsWith(rootDir) && p.getParent != rootDir && p.getParent.getParent == rootDir
      },
      FileVisitOption.FOLLOW_LINKS
    )

    private val pathIter = pathStream.iterator()

    private var closed = false

    override def isClosed: Boolean = closed

    override def close(): Unit = {
      try {
        pathStream.close()
      }
      finally {
        closed = true
      }
    }

    override def hasNext: Boolean = {
      checkNotClosed()
      pathIter.hasNext
    }

    override def next(): FsFile = {
      checkNotClosed()
      FsFile(FsPath(pathIter.next()))
    }

    private def checkNotClosed(): Unit = {
      Check.state(!isClosed, "unable to perform operation because the iterator is closed")
    }
  }
}

object LocalFsStorage {
  def apply(rootDir: Path): LocalFsStorage = {
    Check.argNotNull(rootDir, "rootDir")
    new LocalFsStorage(rootDir.toAbsolutePath)
  }

  def apply(rootDir: String): FileStorage = apply(Paths.get(rootDir))
}