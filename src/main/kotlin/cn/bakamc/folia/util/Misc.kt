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

infix fun SerializeElement.completeEquals(target: SerializeElement): Boolean {
    if (this is SerializePrimitive && target is SerializePrimitive) {
        return when {
            isString && target.isString         -> asString == target.asString
            isBoolean && target.isBoolean       -> asBoolean == target.asBoolean
            isNumber && target.isNumber         -> asNumber.toDouble() == target.asNumber.toDouble()
            isBigDecimal && target.isBigDecimal -> asBigDecimal == target.asBigDecimal
            isBigInteger && target.isBigInteger -> asBigInteger == target.asBigInteger
            else                                -> false
        }
    } else if (this is SerializeNull && target is SerializeNull) {
        return true
    } else if (this is SerializeArray && target is SerializeArray && this.size == target.size) {
        var result = false
        forEachIndexed { index, element ->
            result = element completeEquals target[index]
        }
        return result
    } else if (this is SerializeObject && target is SerializeObject && this.size == target.size && this.keys == target.keys) {
        var result = false
        forEach { k, v ->
            result = v completeEquals target[k]!!
        }
        return result
    } else return false
}

infix fun SerializeElement.partEquals(target: SerializeArray): Boolean {
    target.contains(this)
    if (this is SerializeArray) {
        target.containsAll(this)
    }else{
        TODO("实现判断")
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
        is CompoundTag            -> toSerializerObjet()
        is CollectionTag<out Tag> -> serializeArray { this@toSerializerElement.forEach { it.toSerializerElement() } }
        is NumericTag             -> SerializePrimitive(this.asNumber)
        is StringTag              -> SerializePrimitive(this.asString)
        else                      -> SerializeNull
    }
}