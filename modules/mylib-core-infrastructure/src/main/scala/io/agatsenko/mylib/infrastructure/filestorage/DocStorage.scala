/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-16
  */
package io.agatsenko.mylib.infrastructure.filestorage

import scala.collection.Factory
import scala.io.Source

import java.io.InputStream
import java.net.URI

import io.mango.common.resource.{using, CloseableResource}

// FIXME: need to remove
trait DocStorage extends CloseableResource {
  type TStorage <: DocStorage
  type TPath <: StorageDocPath
  type TDoc <: Document

  trait StorageDocPath extends Equals {
    this: TPath =>

    def fileName: String

    def uri: URI

    def storage: TStorage
  }

  trait Document {
    this: TDoc =>

    def storage: TStorage

    def path: TPath

    def name: String = path.fileName

    def inputStream: InputStream

    def source(buffSize: Int = Source.DefaultBufSize): Source = Source.createBufferedSource(inputStream, buffSize)
  }

  trait StorageDocIterator extends Iterator[TDoc] with CloseableResource

  def toUri(uriStr: String): URI

  def toPath(uri: URI): TPath

  def toPath(uriStr: String): TPath = toPath(toUri(uriStr))

  def filesIterator: StorageDocIterator

  def exists(path: TPath): Boolean

  def exists(file: TDoc): Boolean = exists(file.path)

  def exists(uri: URI): Boolean = exists(toPath(uri))

  def exists(uriStr: String): Boolean = exists(toPath(uriStr))

  def get(path: TPath): Option[TDoc]

  def get(uri: URI): Option[TDoc] = get(toPath(uri))

  def get(uriStr: String): Option[TDoc] = get(toUri(uriStr))

  def map[T, C[_]](f: TDoc => T)(implicit cf: Factory[T, C[T]]): C[T] = {
    using(filesIterator) { iter =>
      val builder = cf.newBuilder
      for (file <- iter) {
        builder += f(file)
      }
      builder.result()
    }
  }

  def filter[C[_]](f: TDoc => Boolean)(implicit cf: Factory[TDoc, C[TDoc]]): C[TDoc] = {
    using(filesIterator) { iter =>
      val builder = cf.newBuilder
      for (file <- iter) {
        if (f(file)) {
          builder += file
        }
      }
      builder.result()
    }
  }

  def foreach[U](f: TDoc => U): Unit = {
    using(filesIterator) { iter =>
      for (file <- iter) {
        f(file)
      }
    }
  }

  def putNew(fileName: String, in: InputStream): TDoc

  def put(path: TPath, in: InputStream): TDoc

  def put(file: TDoc, in: InputStream): Unit = put(file.path, in)

  def put(uri: URI, in: InputStream): TDoc = put(toPath(uri), in)

  def put(uriStr: String, in: InputStream): TDoc = put(toPath(uriStr), in)

  def remove(path: TPath): Boolean

  def remove(file: TDoc): Boolean = remove(file.path)

  def remove(uri: URI): Boolean = remove(toPath(uri))

  def remove(uriStr: String): Boolean = remove(toPath(uriStr))
}
