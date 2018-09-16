/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-16
  */
package io.agatsenko.mylib.infrastructure.filestorage.localfs

import java.io.InputStream
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

  private val rootDirStr = rootDir.toString

  Check.argNotNull(rootDir, "rootDir")
  Check.arg(rootDir.isAbsolute, s"rootDir ($rootDir) is not absolute")
  Check.arg(Files.exists(rootDir), s"rootDir ($rootDir) is not exist")
  Check.arg(Files.isDirectory(rootDir), s"rootDir ($rootDir) is not directory")

  override def toPath(path: String, morePaths: String*): FsPath = {
    FsPath(Paths.get(rootDirStr, morePaths: _*))
  }

  override def exists(path: FsPath): Boolean = {
    Files.exists(path.underlyingPath) && !Files.isDirectory(path.underlyingPath)
  }

  override def get(path: FsPath): Option[FsFile] = if (exists(path)) Some(FsFile(path)) else None

  override def putNew(fileName: String, in: InputStream): FsFile = {
    val fileDirPath = rootDir.resolve(UUID.randomUUID().toString)
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
      Files.delete(path.underlyingPath)
      true
    }
    else {
      false
    }
  }

  override protected def files: Iterator[TFile] = new FilesIterator

  case class FsPath private(underlyingPath: Path) extends StorageFilePath {
    val fileName: String = underlyingPath.getFileName.toString

    override def storage: LocalFsStorage = LocalFsStorage.this
  }

  case class FsFile private(path: FsPath) extends StorageFile {
    override def storage: LocalFsStorage = LocalFsStorage.this
  }

  private class FilesIterator extends Iterator[FsFile] {
    private val underlyingIter = Files.find(
      rootDir,
      1,
      (p: Path, a: BasicFileAttributes) => p.getParent != rootDir && !a.isDirectory,
      FileVisitOption.FOLLOW_LINKS
    ).iterator()

    override def hasNext: Boolean = underlyingIter.hasNext

    override def next(): FsFile = FsFile(FsPath(underlyingIter.next()))
  }
}

object LocalFsStorage {
  def apply(rootDir: Path): LocalFsStorage = {
    Check.argNotNull(rootDir, "rootDir")
    new LocalFsStorage(rootDir.toAbsolutePath)
  }

  def apply(rootDir: String): FileStorage = apply(Paths.get(rootDir))
}