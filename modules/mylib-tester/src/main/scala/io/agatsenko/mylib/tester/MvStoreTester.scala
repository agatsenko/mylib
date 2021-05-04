/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-02-09
 */
package io.agatsenko.mylib.tester

import scala.util.Using

import java.util.UUID

import org.h2.mvstore.MVStore

object MvStoreTester {
  def main(args: Array[String]): Unit = {
    Using(new MVStore.Builder().autoCommitDisabled().open()) { store =>
      val map1 = store.openMap[UUID, String]("map1")
      val id = UUID.randomUUID()
      map1.put(UUID.randomUUID(), "one")
      map1.put(id, "two")
      map1.put(UUID.randomUUID(), "three")
      store.commit()

      println(map1.get(id))
    }
  }
}
