@file:Suppress("unused")

package cn.bakamc.folia.util

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.BakaMCPlugin.Companion.PluginScope
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import kotlinx.coroutines.*
import moe.forpleuvoir.nebula.common.util.Time
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeNull
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.extensions.serializeArray
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import net.minecraft.nbt.*
import net.minecraft.server.MinecraftServer
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration

internal val bakamc by lazy { BakaMCPlugin.instance }

internal val server by lazy { bakamc.server }

internal val logger by lazy { Logger(BakaMCPlugin.instance.logger) }

fun Player.toServerPlayer() = MinecraftServer.getServer().playerList.getPlayer(this.uniqueId)

internal fun syncTask(task: () -> Unit) {
    server.scheduler.scheduleSyncDelayedTask(bakamc, task)
}

internal fun launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = PluginScope.launch(context, start, block)


internal fun <T> async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): Deferred<T> = PluginScope.async(context, start, block)


fun runNow(plugin: Plugin, action: (ScheduledTask) -> Unit) {
    plugin.server.asyncScheduler.runNow(plugin, action)
}

internal fun runNow(action: (ScheduledTask) -> Unit) {
    runNow(BakaMCPlugin.instance, action)
}

fun runDelayed(plugin: Plugin, delay: Duration, task: (ScheduledTask) -> Unit) {
    plugin.server.asyncScheduler.runDelayed(plugin, task, delay.inWholeMilliseconds, TimeUnit.MILLISECONDS)
}

internal fun runDelayed(delay: Duration, task: (ScheduledTask) -> Unit) {
    runDelayed(bakamc, delay, task)
}

fun runAtFixedRate(plugin: Plugin, initialDelay: Duration, period: Duration, task: (ScheduledTask) -> Unit) {
    plugin.server.asyncScheduler.runAtFixedRate(plugin, task, initialDelay.inWholeMilliseconds, period.inWholeMilliseconds, TimeUnit.MILLISECONDS)
}

internal fun runAtFixedRate(initialDelay: Duration, period: Duration, task: (ScheduledTask) -> Unit) {
    runAtFixedRate(bakamc, initialDelay, period, task)
}


fun runAtFixedRate(asyncTask: AsyncTask) {
    asyncTask.plugin.server.asyncScheduler.runAtFixedRate(
        asyncTask.plugin,
        asyncTask.task,
        asyncTask.initialDelay.inWholeMilliseconds,
        asyncTask.period.inWholeMilliseconds,
        TimeUnit.MILLISECONDS
    )
}

data class AsyncTask(
    val initialDelay: Duration,
    val period: Duration,
    val plugin: Plugin,
    val task: (ScheduledTask) -> Unit,
) {
    internal constructor(initialDelay: Duration, period: Duration, task: (ScheduledTask) -> Unit) : this(initialDelay, period, bakamc, task)

    constructor(initialDelay: Time, period: Time, plugin: Plugin, task: (ScheduledTask) -> Unit) : this(
        initialDelay.duration,
        period.duration,
        plugin,
        task
    )

    internal constructor(initialDelay: Time, period: Time, task: (ScheduledTask) -> Unit) : this(
        initialDelay.duration,
        period.duration,
        bakamc,
        task
    )

    constructor(initialDelay: Duration, period: Time, plugin: Plugin, task: (ScheduledTask) -> Unit) : this(
        initialDelay,
        period.duration,
        plugin,
        task
    )

    internal constructor(initialDelay: Duration, period: Time, task: (ScheduledTask) -> Unit) : this(
        initialDelay,
        period.duration,
        bakamc,
        task
    )

    constructor(initialDelay: Time, period: Duration, plugin: Plugin, task: (ScheduledTask) -> Unit) : this(
        initialDelay.duration,
        period,
        plugin,
        task
    )

    internal constructor(initialDelay: Time, period: Duration, task: (ScheduledTask) -> Unit) : this(
        initialDelay.duration,
        period,
        bakamc,
        task
    )
}

fun <T : Comparable<T>> ClosedRange<T>.serialization(): SerializeElement {
    return SerializePrimitive("${this.start}..${this.endInclusive}")
}

fun <T : Comparable<T>> deserialization(serializeElement: SerializeElement, supplier: (String) -> T): ClosedRange<T> {
    serializeElement as SerializePrimitive
    serializeElement.asString.let {
        val pair = it.split("..")
        return supplier(pair[0])..supplier(pair[1])
    }
}

fun CompoundTag.toSerializerObjet(): SerializeObject {
    return serializeObject {
        tags.forEach { (k, v) ->
            k - v.toSerializerElement()
        }
    }
}

fun Tag.toSerializerElement(): SerializeElement {
    return when (this) {
        is ShortTag               -> SerializePrimitive(this.asShort)
        is DoubleTag              -> SerializePrimitive(this.asDouble)
        is FloatTag               -> SerializePrimitive(this.asFloat)
        is ByteTag                -> SerializePrimitive(this.asByte)
        is IntTag                 -> SerializePrimitive(this.asInt)
        is LongTag                -> SerializePrimitive(this.asLong)
        is StringTag              -> SerializePrimitive(this.asString)
        is NumericTag             -> SerializePrimitive(this.asNumber)
        is LongArrayTag           -> serializeArray { this@toSerializerElement.forEach { SerializePrimitive(it.asLong) } }
        is ByteArrayTag           -> serializeArray { this@toSerializerElement.forEach { SerializePrimitive(it.asByte) } }
        is IntArrayTag            -> serializeArray { this@toSerializerElement.forEach { SerializePrimitive(it.asInt) } }
        is ListTag                -> serializeArray { this@toSerializerElement.forEach { it.toSerializerElement() } }
        is CollectionTag<out Tag> -> serializeArray { this@toSerializerElement.forEach { it.toSerializerElement() } }
        is CompoundTag            -> toSerializerObjet()
        else                      -> SerializeNull
    }
}
