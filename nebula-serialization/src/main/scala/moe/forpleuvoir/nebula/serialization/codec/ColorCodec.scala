package moe.forpleuvoir.nebula.serialization.codec

import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.serialization.base.{SerializeArray, SerializeElement, SerializeObject, SerializePrimitive}
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.nebula.serialization.extension.SerObjectOps.*

import scala.util.{Success, Try}

private def decodeColor(data: SerializeElement): Color = data match {
  case primitive: SerializePrimitive =>
    if (primitive.isNumber) Color.fromARGB(primitive.asInt.get)
    else if (primitive.isString) Color.fromHexString(primitive.asString.get)
    else throw new IllegalArgumentException("Invalid input: expected an number or string, but got a " + primitive.getClass.getName)
  case obj: SerializeObject =>
    if (obj.containsKeys("red", "green", "blue")) {
      val red: Int | Float = obj.getInt("red").getOrElse(obj.getAsFloat("red").get)
      val green: Int | Float = obj.getInt("green").getOrElse(obj.getAsFloat("green").get)
      val blue: Int | Float = obj.getInt("blue").getOrElse(obj.getAsFloat("blue").get)
      val alpha: Int | Float = Try(obj.getInt("alpha").getOrElse(obj.getAsFloat("alpha").get)).getOrElse(255)
      Color.fromARGB(red, green, blue, alpha)
    } else if (obj.containsKeys("hue", "saturation", "value")) {
      val hue: Int | Float = obj.getInt("hue").getOrElse(obj.getAsFloat("hue").get)
      val saturation: Int | Float = obj.getInt("saturation").getOrElse(obj.getAsFloat("saturation").get)
      val value: Int | Float = obj.getInt("value").getOrElse(obj.getAsFloat("value").get)
      val alpha: Int | Float = Try(obj.getInt("alpha").getOrElse(obj.getAsFloat("alpha").get)).getOrElse(255)
      Color.fromHSV(hue, saturation, value, alpha)
    } else throw new IllegalArgumentException("Invalid input: couldn't find either HSV (hue, saturation, value) or RGB (red, green, blue) color data in the provided object. Please ensure the input object contains the required keys.")
  case SerializeArray(elements) =>
    if (elements.size == 3 || elements.size == 4) {
      val red: Int | Float = elements.head.asInt.getOrElse(elements.head.asFloat.get)
      val green: Int | Float = elements(1).asInt.getOrElse(elements(1).asFloat.get)
      val blue: Int | Float = elements(2).asInt.getOrElse(elements(2).asFloat.get)
      val alpha: Int | Float = if (elements.size == 4) elements(3).asInt.getOrElse(elements(3).asFloat.get) else 255
      Color.fromARGB(red, green, blue, alpha)
    } else throw new IllegalArgumentException("Invalid input: expected an array of size 3 or 4, but got an array of size " + elements.size)
  case _ => throw new IllegalArgumentException("Invalid input: expected a SerializeObject, but got a " + data.getClass.getName)
}

object ColorCodec extends Codec[Color] {

  override def deserialization(data: SerializeElement): Try[Color] = Try(decodeColor(data))

  override def serialization(value: Color): SerializeElement = SerializePrimitive(value.toHex)

}

class ColorDefaultableCodec(default: Color) extends Codec[Color] {
  override def deserialization(data: SerializeElement): Try[Color] = Success {
    ColorCodec.deserialization(data).getOrElse(default)
  }

  override def serialization(value: Color): SerializeElement = ColorCodec.serialization(value)
}

object ColorDefaultableCodec {
  def default(default: Color): ColorDefaultableCodec = new ColorDefaultableCodec(default)
}

given Codec[Color] = ColorCodec

