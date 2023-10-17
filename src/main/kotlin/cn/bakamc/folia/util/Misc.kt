package cn.bakamc.folia.util

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.db.session
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeNull
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
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


val logger by lazy { BakaMCPlugin.logger }

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
        is CollectionTag<out Tag> -> {
            serializeArray {
                this@toSerializerElement.forEach {
                    it.toSerializerElement()
                }
            }
        }

        is NumericTag                -> {
            SerializePrimitive(this.asNumber)
        }

        is StringTag                 -> {
            SerializePrimitive(this.asString)
        }

        else                      -> {
            SerializeNull
        }
    }
}