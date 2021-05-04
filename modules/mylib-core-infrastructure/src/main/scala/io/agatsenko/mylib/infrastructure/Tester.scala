/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-10-01
  */
package io.agatsenko.mylib.infrastructure

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration.Duration
import scala.io.StdIn
import scala.util.control.NonFatal

import java.nio.ByteBuffer
import java.nio.channels.{AsynchronousFileChannel, CompletionHandler}
import java.nio.file.{Files, Path, Paths}
import java.nio.file.StandardOpenOption.{CREATE, READ, TRUNCATE_EXISTING, WRITE}

// FIXME: need to remove
object Tester {
  def main(args: Array[String]): Unit = {
//    nonBlockingNIO()
  }

  private def nonBlockingNIO(): Unit = {
    val filePath = Paths.get("D:\\agat\\dev\\projects\\mylib\\files\\test.txt")
    val buffSize = 1024 * 1024

    val writtenBytes = Await.result(write(filePath, buildText(1000).getBytes, buffSize), Duration.Inf)
    println(s"written bytes: $writtenBytes")

    println("press enter to continue ...")
    StdIn.readLine()

    val text = Await.result(read(filePath, buffSize), Duration.Inf)
    text.split("\n").zipWithIndex.foreach(t => println(s"${t._2 + 1}: ${t._1}"))
  }
  private def buildText(linesCount: Int): String = {
    val line = "Hello World !!!\n"

    val sb = new StringBuilder
    for (_ <- 1 to linesCount) {
      sb.append(line)
    }
    sb.toString()
  }

  private def write(filePath: Path, bytes: Array[Byte], buffCapacity: Int): Future[Int] = {
    case class WriteHandler(
        bytes: Array[Byte],
        channel: AsynchronousFileChannel,
        buffer: ByteBuffer) extends CompletionHandler[Integer, Int] {
      val promise = Promise[Int]()

      override def completed(writtenBytes: Integer, overallWrittenBytes: Int): Unit = {
        val newOverallWrittenBytes = overallWrittenBytes + writtenBytes
        try {
          if (newOverallWrittenBytes >= bytes.length) {
            channel.close()
            promise.success(newOverallWrittenBytes)
          }
          else {
            fillBuff(newOverallWrittenBytes)
            channel.write(buffer, newOverallWrittenBytes, newOverallWrittenBytes, this)
          }
        }
        catch {
          case NonFatal(ex) => promise.failure(ex)
        }
      }

      override def failed(err: Throwable, ignore: Int): Unit = {
        promise.failure(err)
      }

      private def fillBuff(startIndx: Int): Unit = {
        buffer.clear()
        val putLen = if (bytes.length - startIndx > buffer.capacity()) buffer.capacity() else bytes.length - startIndx
        buffer.put(bytes, startIndx, putLen)
        buffer.position(0)
        buffer.limit(putLen)
      }
    }

    val buff = ByteBuffer.allocateDirect(buffCapacity)
    buff.limit(0)
    val channel = AsynchronousFileChannel.open(filePath, CREATE, TRUNCATE_EXISTING, WRITE)
    try {
      val handler = WriteHandler(bytes, channel, buff)
      channel.write(buff, 0, 0, handler)
      handler.promise.future
    }
    catch {
      case NonFatal(ex) =>
        channel.close()
        throw ex
    }
  }

  private def read(filePath: Path, buffCapacity: Int): Future[String] = {
    import scala.concurrent.ExecutionContext.Implicits.global

    case class ReadHandler(
        channel: AsynchronousFileChannel,
        buffer: ByteBuffer,
        bytes: Array[Byte]) extends CompletionHandler[Integer, Int] {
      val promise = Promise[Int]

      override def completed(result: Integer, overallReadBytes: Int): Unit = {
        val newOverallReadBytes = result + overallReadBytes

        try {
          fillBytes(overallReadBytes)
          if (newOverallReadBytes >= bytes.length) {
            channel.close()
            promise.success(newOverallReadBytes)
          }
          else {
            channel.read(buffer, newOverallReadBytes, newOverallReadBytes, this)
          }
        }
        catch {
          case NonFatal(ex) => promise.failure(ex)
        }
      }

      override def failed(ex: Throwable, ignore: Int): Unit = {
        promise.failure(ex)
      }

      private def fillBytes(startIndx: Int): Unit = {
        val getBytes = buffer.position()
        if (getBytes > 0) {
          buffer.position(0)
          buffer.get(bytes, startIndx, getBytes)
        }
        buffer.clear()
      }
    }

    val bytes = new Array[Byte](Files.size(filePath).toInt)
    val buff = ByteBuffer.allocateDirect(buffCapacity)
    val channel = AsynchronousFileChannel.open(filePath, READ)
    try {
      val handler = ReadHandler(channel, buff, bytes)
      channel.read(buff, 0, 0, handler)
      handler.promise.future.map(_ => new String(bytes))
    }
    catch {
      case NonFatal(ex) =>
        channel.close()
        throw ex
    }
  }
}
