/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-16
  */
package io.agatsenko.mylib.infrastructure.filestorage

import scala.language.higherKinds

import scala.collection.generic.CanBuildFrom
import scala.io.Source

import java.io.InputStream
import java.net.URI

import io.mango.common.resource.{using, CloseableResource}

trait FileStorage extends CloseableResource {
  type TStorage <: FileStorage
  type TPath <: StorageFilePath
  type TFile <: StorageFile

  trait StorageFilePath extends Equals {
    this: TPath =>

    def fileName: String

    def uri: URI

    def storage: TStorage
  }

  trait StorageFile {
    this: TFile =>

    def storage: TStorage

    def path: TPath

    def name: String = path.fileName

    def inputStream: InputStream

    def source(buffSize: Int = Source.DefaultBufSize): Source = Source.createBufferedSource(inputStream, buffSize)
  }

  trait StorageFileIterator extends Iterator[TFile] with CloseableResource

  def toUri(uriStr: String): URI

  def toPath(uri: URI): TPath

  def toPath(uriStr: String): TPath = toPath(toUri(uriStr))

  def filesIterator: StorageFileIterator

  def exists(path: TPath): Boolean

  def exists(file: TFile): Boolean = exists(file.path)

  def exists(uri: URI): Boolean = exists(toPath(uri))

  def exists(uriStr: String): Boolean = exists(toPath(uriStr))

  def get(path: TPath): Option[TFile]

  def get(uri: URI): Option[TFile] = get(toPath(uri))

  def get(uriStr: String): Option[TFile] = get(toUri(uriStr))

  def map[T, C[_]](f: TFile => T)(implicit cbf: CanBuildFrom[_, T, C[T]]): C[T] = {
    using(filesIterator) { iter =>
      val builder = cbf()
      for (file <- iter) {
        builder += f(file)
      }
      builder.result()
    }
  }

  def filter[C[_]](f: TFile => Boolean)(implicit cbf: CanBuildFrom[_, TFile, C[TFile]]): C[TFile] = {
    using(filesIterator) { iter =>
      val builder = cbf()
      for (file <- iter) {
        if (f(file)) {
          builder += file
        }
      }
      builder.result()
    }
  }

  def foreach[U](f: TFile => U): Unit = {
    using(filesIterator) { iter =>
      for (file <- iter) {
        f(file)
      }
    }
  }

  def putNew(fileName: String, in: InputStream): TFile

  def put(path: TPath, in: InputStream): TFile

  def put(file: TFile, in: InputStream): Unit = put(file.path, in)

  def put(uri: URI, in: InputStream): Unit = put(toPath(uri), in)

  def put(uriStr: String, in: InputStream): Unit = put(toPath(uriStr), in)

  def remove(path: TPath): Boolean

  def remove(file: TFile): Boolean = remove(file.path)

  def remove(uri: URI): Boolean = remove(toPath(uri))

  def remove(uriStr: String): Boolean = remove(toPath(uriStr))
}
