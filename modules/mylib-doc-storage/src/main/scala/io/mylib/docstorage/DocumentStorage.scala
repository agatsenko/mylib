/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2019-09-25
 */
package io.mylib.docstorage

import scala.collection.Factory
import scala.io.Source

import java.io.InputStream
import java.net.URI

import io.mango.common.resource.{using, CloseableResource}

trait DocumentStorage extends CloseableResource {
  type TStorage <: DocumentStorage
  type TPath <: DocumentPath
  type TDocument <: Document
  type TDocumentIterator <: DocumentIterator

  trait DocumentPath extends Equals {
    this: TPath =>

    def docName: String

    def uri: URI

    def storage: TStorage
  }

  trait Document {
    this: TDocument =>

    def path: TPath

    def name: String = path.docName

    def inputStream(): InputStream

    def source(buffSize: Int = Source.DefaultBufSize): Source = Source.createBufferedSource(inputStream, buffSize)

    def storage: TStorage
  }

  trait DocumentIterator extends Iterator[TDocument] with CloseableResource

  def toUri(uriStr: String): URI

  def toPath(uri: URI): TPath

  def toPath(uriStr: String): TPath = toPath(toUri(uriStr))

  def docsIterator: DocumentIterator

  def exists(path: TPath): Boolean

  def exists(doc: TDocument): Boolean = exists(doc.path)

  def exists(uri: URI): Boolean = exists(toPath(uri))

  def exists(uriStr: String): Boolean = exists(toPath(uriStr))

  def get(path: TPath): Option[TDocument]

  def get(uri: URI): Option[TDocument] = get(toPath(uri))

  def get(uriStr: String): Option[TDocument] = get(toUri(uriStr))

  def map[T, C[_]](f: TDocument => T)(implicit cf: Factory[T, C[T]]): C[T] = {
    using(docsIterator) { iter =>
      val builder = cf.newBuilder
      for (doc <- iter) {
        builder += f(doc)
      }
      builder.result()
    }
  }

  def filter[C[_]](f: TDocument => Boolean)(implicit cf: Factory[TDocument, C[TDocument]]): C[TDocument] = {
    using(docsIterator) { iter =>
      val builder = cf.newBuilder
      for (doc <- iter) {
        if (f(doc)) {
          builder += doc
        }
      }
      builder.result()
    }
  }

  def foreach[U](f: TDocument => U): Unit = {
    using(docsIterator) { iter =>
      for (doc <- iter) {
        f(doc)
      }
    }
  }

  def putNew(docName: String, in: InputStream): TDocument

  def put(path: TPath, in: InputStream): TDocument

  def put(doc: TDocument, in: InputStream): Unit = put(doc.path, in)

  def put(uri: URI, in: InputStream): TDocument = put(toPath(uri), in)

  def put(uriStr: String, in: InputStream): TDocument = put(toPath(uriStr), in)

  def remove(path: TPath): Boolean

  def remove(doc: TDocument): Boolean = remove(doc.path)

  def remove(uri: URI): Boolean = remove(toPath(uri))

  def remove(uriStr: String): Boolean = remove(toPath(uriStr))
}
