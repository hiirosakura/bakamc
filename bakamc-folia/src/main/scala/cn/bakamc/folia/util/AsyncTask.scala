package cn.bakamc.folia.util

import cn.bakamc.folia.BakaMC
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.plugin.java.JavaPlugin

import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import scala.concurrent.duration.FiniteDuration

case class AsyncTask(
  initialDelay: FiniteDuration,
  period: FiniteDuration,
  plugin: JavaPlugin,
  task: ScheduledTask => Unit
) {
  @volatile private var scheduledTask: Option[ScheduledTask] = None
}

object AsyncTask {

  private[folia] def apply(initialDelay: FiniteDuration, period: FiniteDuration)(task: ScheduledTask => Unit): AsyncTask =
    AsyncTask(initialDelay, period, BakaMC, task)

  extension (task: AsyncTask) {

    def run(): Unit = {
      cancel()

      val _task = task.plugin.getServer.getAsyncScheduler.runAtFixedRate(
        task.plugin,
        task.task.asInstanceOf[Consumer[ScheduledTask]],
        task.initialDelay.toMillis,
        task.period.toMillis,
        TimeUnit.MILLISECONDS
      )
      task.scheduledTask = Some(_task)
    }

    def cancel(): Unit = {
      task.scheduledTask.foreach(_.cancel())
    }

  }

}


def runDelayed(plugin: JavaPlugin, delay: FiniteDuration)(task: ScheduledTask => Unit): Unit = {
  plugin.getServer.getAsyncScheduler.runDelayed(plugin, task.asInstanceOf[Consumer[ScheduledTask]], delay.toMillis, TimeUnit.MILLISECONDS)
}

private[folia] def runDelayed(delay: FiniteDuration)(task: ScheduledTask => Unit): Unit = {
  runDelayed(BakaMC, delay)(task)
}

def runAtFixedRate(plugin: JavaPlugin, initialDelay: FiniteDuration, period: FiniteDuration)(task: ScheduledTask => Unit): Unit = {
  plugin.getServer.getAsyncScheduler.runAtFixedRate(plugin, task.asInstanceOf[Consumer[ScheduledTask]], initialDelay.toMillis, period.toMillis, TimeUnit.MILLISECONDS)
}

private[folia] def runAtFixedRate(initialDelay: FiniteDuration, period: FiniteDuration)(task: ScheduledTask => Unit): Unit = {
  runAtFixedRate(BakaMC, initialDelay, period)(task)
}