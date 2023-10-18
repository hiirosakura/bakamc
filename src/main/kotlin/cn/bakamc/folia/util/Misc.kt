package cn.bakamc.folia.util

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.db.session
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import moe.forpleuvoir.nebula.serialization.base.*
import moe.forpleuvoir.nebula.serialization.extensions.serializeArray
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import net.minecraft.nbt.*
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@OptIn(ExperimentalContracts::class)
inline fun <reified T : BaseMapper<*>> mapper(action: T.() -> Unit) {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    session {
        getMapper(T::class.java).action()
    }
}


val logger by lazy { BakaMCPlugin.insctence.logger }

fun timerTask(run: () -> Unit): TimerTask {
    return object : TimerTask() {
        override fun run() {
            run()
        }
    }
}

fun Timer.schedule(task: SimpleTimerTask) {
    this.schedule(task.task, task.delay, task.period)
}

class SimpleTimerTask(
    val delay: Long,
    val period: Long,
    task: () -> Unit
) {
    val task = timerTask(task)
}

fun runNow(action: (ScheduledTask) -> Unit) {
    BakaMCPlugin.insctence.server.asyncScheduler.runNow(BakaMCPlugin.insctence, action)
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