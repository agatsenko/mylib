/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2019-09-25
 */
package io.mylib.docstorage

class DocumentStorageException(message: String, cause: Throwable) extends RuntimeException(message, cause) {
  def this(message: String) = {
    this(message, null)
  }

  def this(cause: Throwable) = {
    this(if (cause == null) null else cause.getMessage, cause)
  }
}
