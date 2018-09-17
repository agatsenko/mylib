/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-17
  */
package io.agatsenko.mylib.infrastructure

import scala.io.Source

import java.io.ByteArrayInputStream

import _root_.io.agatsenko.mylib.infrastructure.filestorage.localfs.LocalFsStorage
import _root_.io.mango.common.resource.using

object Tester {
  def main(args: Array[String]): Unit = {
    val files = Seq("one", "Hello World !!!", "three")
    val fs = LocalFsStorage("D:\\agat\\dev\\projects\\mylib\\files\\")

    // create files
    files.zipWithIndex.foreach {
      case (inStr, index) =>
        using(new ByteArrayInputStream(inStr.getBytes)) { in =>
          fs.putNew(s"file${index + 1}", in)
        }
    }

    fs.foreach { f =>
      println(s"$f")
      using(Source.fromInputStream(f.inputStream)) { source =>
        source.getLines().foreach(println)
      }
      println()
      fs.remove(f)
    }
  }
}
