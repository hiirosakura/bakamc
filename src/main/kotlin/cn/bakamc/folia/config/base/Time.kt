package cn.bakamc.folia.config.base

import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import java.util.concurrent.TimeUnit

data class Time(
    val time: Long,
    val unit: TimeUnit
) : Serializable {
    override fun serialization(): SerializeElement {
        return serializeObject {
            "time" - time
            "unit" - unit.name
        }
    }

    companion object {

        fun deserialization(serializeElement: SerializeElement): Time {
            serializeElement as SerializeObject
            val time = serializeElement["time"]!!.asLong
            val unit = TimeUnit.valueOf(serializeElement["unit"]!!.asString)
            return Time(time, unit)
        }

    }


}