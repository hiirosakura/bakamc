package cn.bakamc.folia.config.base

import moe.forpleuvoir.nebula.common.util.clamp
import moe.forpleuvoir.nebula.config.ConfigBase
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import java.util.concurrent.TimeUnit

class ConfigTime(
    override val key: String,
    override val defaultValue: Time,
    val minValue: Long = Long.MIN_VALUE,
    val maxValue: Long = Long.MAX_VALUE
) : ConfigBase<Time, ConfigTime>() {

    constructor(
        key: String,
        defaultValueTime: Long,
        defaultTimeUnit: TimeUnit,
        minValue: Long = Long.MIN_VALUE,
        maxValue: Long = Long.MAX_VALUE
    ) : this(
        key,
        Time(defaultValueTime, defaultTimeUnit),
        minValue,
        maxValue
    )

    override var configValue: Time = Time(defaultValue.time.clamp(minValue, maxValue), defaultValue.unit)

    override fun setValue(value: Time) {
        super.setValue(Time(value.time.clamp(minValue, maxValue), value.unit))
    }

    override fun deserialization(serializeElement: SerializeElement) {
        configValue = Time.deserialization(serializeElement).run {
            Time(time.clamp(minValue, maxValue), unit)
        }
    }

    override fun serialization(): SerializeElement {
        return configValue.serialization()
    }


}