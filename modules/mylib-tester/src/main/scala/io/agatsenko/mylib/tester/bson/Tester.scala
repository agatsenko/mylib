/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-03-08
 */
package io.agatsenko.mylib.tester.bson

import scala.util.Using

import java.nio.ByteBuffer

import org.bson.{BsonBinaryReader, BsonBinaryWriter, BsonWriter}
import org.bson.io.BasicOutputBuffer
import org.bson.AbstractBsonReader.State

object Tester {
  def main(args: Array[String]): Unit = {
    val srcDoc = Doc.generate()

    val bytes = write(srcDoc)
    val readDoc = read(bytes)

    println(s"srcDoc:  $srcDoc")
    println(s"readDoc: $readDoc")
  }

  def writeDocFields(writer: BsonWriter, doc: Doc): Unit = {
    writer.writeInt32("intVal", doc.intVal)
    writer.writeString("strVal", doc.strVal)
    writer.writeStartArray("ints")
    doc.ints.foreach(writer.writeInt32)
    writer.writeEndArray()
  }

  def write(doc: Doc): Array[Byte] = {
    Using.Manager { use =>
      val outBuff = use(new BasicOutputBuffer())
      val writer = use(new BsonBinaryWriter(outBuff))

      writer.writeStartDocument()
      writeDocFields(writer, doc)
      writer.writeEndDocument()
      writer.flush()
      outBuff.flush()
      outBuff.toByteArray
    }.get
  }

  def read(bytes: Array[Byte]): Doc = {
    val buff = ByteBuffer.wrap(bytes)
    Using(new BsonBinaryReader(buff)) { reader =>
      reader.readStartDocument()
      reader.readBsonType()
      reader.readName()
      val intVal = reader.readInt32()
      reader.readBsonType()
      reader.readName()
      val strVal = reader.readString()
      reader.readBsonType()
      reader.readName()
      reader.readStartArray()
      val intsBuilder = Seq.newBuilder[Int]
      while (reader.getState != State.END_OF_ARRAY) {
        reader.readBsonType()
        if (reader.getState == State.VALUE) {
          intsBuilder += reader.readInt32().intValue()
        }
      }
      reader.readEndArray()
      return Doc(intVal, strVal, intsBuilder.result(), None)
    }
  }.get
}
