package cn.bakamc.folia.functional.specialitem

import cn.bakamc.folia.BakaMC
import cn.bakamc.folia.database.services.SpecialItemService
import cn.bakamc.folia.database.table.SpecialItem
import moe.forpleuvoir.nebula.common.api.Initializable

import java.util.concurrent.ConcurrentHashMap
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.jdk.CollectionConverters.*
import language.implicitConversions

object SpecialItemManager extends Initializable {

  private[specialitem] val timeoutDuration: FiniteDuration = 20.second

  private[specialitem] val cache = ConcurrentHashMap[String, SpecialItem]()

  private[specialitem] def logger = BakaMC.logger

  private[specialitem] def service = SpecialItemService

  override def initialization(): Unit = {
    cache.clear()
    try {
      Await.result(service.getSpecialItems, timeoutDuration)
        .foreach { item =>
          cache.put(item.id, item)
        }
      logger.info("特殊物品加载完成 {}", cache.size)
    } catch {
      case e: Throwable => logger.error("特殊物品加载失败", e)
    }
  }

  def getCache: Map[String, SpecialItem] = cache.asScala.toMap

  def getCachedItem(id: String): Option[SpecialItem] = Option(cache.get(id))

  def update(item: SpecialItem)(result: Boolean => Unit): Unit = {
    service.inertOrUpdate(item).foreach {
      case r if r > 0 =>
        cache.put(item.id, item)
        result(true)
      case _ => result(false)
    }
  }

  def delete(id: String)(result: Boolean => Unit): Unit = {
    service.delete(id).foreach {
      case r if r > 0 =>
        cache.remove(id)
        result(true)
      case _ => result(false)
    }
  }

  def specifyType(ids: Set[String]): Map[String, SpecialItem] = {
    val builder = Map.newBuilder[String, SpecialItem]
    ids.foreach { id =>
      getCachedItem(id).foreach { item =>
        builder.addOne(id -> item)
      }
    }
    builder.result()
  }

}
