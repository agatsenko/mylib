/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-16
  */
package io.agatsenko.mylib.infrastructure.filestorage

import scala.language.higherKinds

import scala.collection.generic.CanBuildFrom

import java.io.InputStream

import io.mango.common.resource.{using, CloseableResource}

trait FileStorage {
  type TStorage <: FileStorage
  type TPath <: StorageFilePath
  type TFile <: StorageFile

  trait StorageFilePath extends Equals {
    this: TPath =>

    def fileName: String

    def storage: TStorage
  }

  trait StorageFile {
    this: TFile =>

    def storage: TStorage

    def path: TPath

    def name: String = path.fileName

    def inputStream: InputStream
  }

  trait StorageFileIterator extends Iterator[TFile] with CloseableResource

  def toPath(path: String, morePaths: String*): TPath

  def files: StorageFileIterator

  def exists(path: TPath): Boolean

  def exists(file: TFile): Boolean = exists(file.path)

  def get(path: TPath): Option[TFile]

  def get(path: String, morePaths: String*): Option[TFile] = get(toPath(path, morePaths: _*))

  def map[T, C[_]](f: TFile => T)(implicit cbf: CanBuildFrom[_, T, C[T]]): C[T] = {
    using(files) { iter =>
      val builder = cbf()
      for (file <- iter) {
        builder += f(file)
      }
      builder.result()
    }
  }

  def filter[C[_]](f: TFile => Boolean)(implicit cbf: CanBuildFrom[_, TFile, C[TFile]]): C[TFile] = {
    using(files) { iter =>
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
    using(files) { iter =>
      for (file <- iter) {
        f(file)
      }
    }
  }

  def putNew(fileName: String, in: InputStream): TFile

  def put(path: TPath, in: InputStream): TFile

  def put(file: TFile, in: InputStream): Unit = put(file.path, in)

  def remove(path: TPath): Boolean

  def remove(file: TFile): Boolean = remove(file.path)
}
