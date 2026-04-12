package moe.forpleuvoir.nebula.config

import moe.forpleuvoir.nebula.common.util.measureTime
import moe.forpleuvoir.nebula.config.component.ConfigManagerComponent

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

class ConfigManager(
  name: String
)(using private[config] val exceptionHandler: ExceptionHandler = ExceptionHandler.Terminal) extends ConfigGroup(name) {

  given ConfigManager = this

  given ExecutionContext = ExecutionContext.global

  override def parent: Option[ConfigGroup] = None

  override def root: Option[ConfigManager] = Some(this)

  private val components: mutable.Buffer[ConfigManagerComponent] = mutable.Buffer()

  def addComponents[T <: ConfigManagerComponent](component: T): T = {
    components.append(component)
    component
  }

  override def initialization(): Unit = {
    components.foreach(_.beginInit())
    super.initialization()
    components.foreach(_.finishInit())
  }

  private var shouldSave: Boolean = false

  def markSavable(): Unit = {
    shouldSave = true
  }

  def markSaved(): Unit = {
    shouldSave = false
  }

  def savable: Boolean = shouldSave

  def save(): FiniteDuration = {
    val time = measureTime {
      components.foreach(_.onSave())
    }._2
    onSave.apply(time)
    time
  }

  def asyncSave(): Future[FiniteDuration] = Future(save())

  def forceSave(): FiniteDuration = {
    val time = measureTime {
      components.foreach(_.onForcedSave())
    }._2
    onSave.apply(time)
    time
  }

  def asyncForceSave: Future[FiniteDuration] = Future(forceSave())

  def load(): FiniteDuration = {
    val time = measureTime {
      components.foreach(_.onLoad())
    }._2
    onLoad.apply(time)
    time
  }

  def asyncLoad(): Future[FiniteDuration] = Future(load())

  def onSave(callback: FiniteDuration => Unit): Unit = {
    this.onSave = callback
  }

  private var onSave: FiniteDuration => Unit = { b => }

  def onLoad(callback: FiniteDuration => Unit): Unit = {
    this.onLoad = callback
  }

  private var onLoad: FiniteDuration => Unit = { b => }

}


object ConfigManager {

  extension (self: ConfigManager) {

    def startup(): Unit = {
      self.initialization()
      try self.load()
      finally self.forceSave()
    }

  }

}