/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-02-09
 */
package io.agatsenko.mylib.tester.persistence

import scala.io.Source
import scala.util.Using

import java.sql.Connection

object DbInstaller {
  def install(conn: Connection): Unit = {
    Using(conn.prepareStatement(loadSql)) { stmt =>
      stmt.executeUpdate()
    }.get
  }

  def loadSql: String = Source.fromFile(getClass.getResource("/h2.sql").getFile).getLines().mkString("\n")
}
