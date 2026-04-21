package cn.bakamc.folia.database.services

import cn.bakamc.folia.database.db
import cn.bakamc.folia.database.table.{SpecialItem, specialItems}
import slick.jdbc.MySQLProfile.api.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SpecialItemService {

  def getSpecialItems: Future[List[SpecialItem]] = db {
    specialItems.result.map(_.toList)
  }

  def getById(id: String): Future[Option[SpecialItem]] = db {
    specialItems.filter(_.id === id).result.headOption
  }

  def inertOrUpdate(item: SpecialItem): Future[Int] = db {
    specialItems.insertOrUpdate(item)
  }

  def delete(id: String): Future[Int] = db {
    specialItems.filter(_.id === id).delete
  }

}
