package moe.forpleuvoir.nebula.config.item

import moe.forpleuvoir.nebula.config.ConfigWithCodec
import moe.forpleuvoir.nebula.serialization.codec.{DurationCodec, FiniteDurationCodec}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.{Duration, FiniteDuration}

class ConfigFiniteDuration(
  name: String,
  defaultValue: FiniteDuration,
  range: (FiniteDuration, FiniteDuration)
) extends ConfigWithCodec[FiniteDuration](name, defaultValue, FiniteDurationCodec) {
  require(range._1 <= range._2, "min[${range._1}] must be less than or equal to max[${range._2}]")

  private def clamp(v: FiniteDuration, min: FiniteDuration, max: FiniteDuration): FiniteDuration = {
    if (v < min) min
    else if (v > max) max
    else v
  }

  _value = clamp(defaultValue, range._1, range._2)

  override def setValue(value: FiniteDuration): this.type = {
    super.setValue(clamp(value, range._1, range._2))
  }

}

object ConfigFiniteDuration {
  def apply(
    name: String,
    defaultValue: FiniteDuration,
    range: (FiniteDuration, FiniteDuration) = Duration.Zero -> FiniteDuration(Long.MaxValue, TimeUnit.NANOSECONDS)
  ): ConfigFiniteDuration = new ConfigFiniteDuration(name, defaultValue, range)
}

class ConfigDuration(
  name: String,
  defaultValue: Duration,
) extends ConfigWithCodec[Duration](name, defaultValue, DurationCodec)

object ConfigDuration {

  def apply(
    name: String,
    defaultValue: Duration,
  ): ConfigDuration = new ConfigDuration(name, defaultValue)

}