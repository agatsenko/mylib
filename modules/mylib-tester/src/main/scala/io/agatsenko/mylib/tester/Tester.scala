/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-02-09
 */
package io.agatsenko.mylib.tester

import scala.util.Using

import java.sql.ResultSet

import com.typesafe.scalalogging.Logger
import com.zaxxer.hikari.HikariDataSource
import io.mango.sql.Implicits.All._

import io.agatsenko.mylib.tester.model.{Phone, PhoneId}
import io.agatsenko.mylib.tester.persistence.DbInstaller

object Tester {
  val logger = Logger(getClass)

  val connUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"

  def main(args: Array[String]): Unit = {
    logger.info("start")
    val result = Using.Manager { use =>
      val ds = use(createDataSource)
      implicit val conn = use(ds.getConnection)

      DbInstaller.install(conn)

      sql"""
            select * from phones;
            select * from persons;
         """.execRows[Phone, Set](mapPhone).foreach(println)
      println

      sql"select * from phones".execRows[Phone, Seq](mapPhone)
    }
    println(result)
  }

  def createDataSource: HikariDataSource = {
    Class.forName("org.h2.Driver")

    val ds = new HikariDataSource
    ds.setJdbcUrl(connUrl)
    ds.setUsername("sa")
    ds.setPassword("")
    ds
  }

  def mapPhone(rs: ResultSet): Phone = {
    Phone(PhoneId(rs.get[Int]("id")), rs.get[String]("number"))
  }
}
