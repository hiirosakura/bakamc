package cn.bakamc.common.inlinestyletext

import cn.bakamc.common.inlinestyletext.modifier.*
import moe.forpleuvoir.nebula.serialization.base.SerializeObject.:=
import moe.forpleuvoir.nebula.serialization.base.{SerializeElement, SerializeObject}
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.nebula.serialization.codec.Codec.deserialization

given Codec[HoverEventModifier] = HoverEventModifier.CODEC
given Codec[ColorModifier] = ColorModifier.CODEC
given Codec[DecorationModifier] = DecorationModifier.CODEC
given Codec[ClickEventModifier.type] = ClickEventModifier.CODEC
given Codec[LegacyChatFormattingModifier] = LegacyChatFormattingModifier.CODEC

import scala.util.Try

case class ModifierContainer private(
  private val modifiers: List[TextModifier]
) {

  def foreach(f: TextModifier => Unit): Unit = modifiers.foreach(f)

}

object ModifierContainer {

  val DEFAULT = ModifierContainer(
    ClickEventModifier,
    HoverEventModifier.DEFAULT,
    LegacyChatFormattingModifier.DEFAULT,
    DecorationModifier.DEFAULT,
    ColorModifier.DEFAULT,
  )


  val CODEC: Codec[ModifierContainer] = new Codec[ModifierContainer] {

    override def serialization(value: ModifierContainer): SerializeElement = SerializeObject.build {
      value.foreach {
        case ClickEventModifier => "click" := ClickEventModifier
        case h: HoverEventModifier => "hover" := h
        case l: LegacyChatFormattingModifier => "legacy" := l
        case d: DecorationModifier => "decoration" := d
        case c: ColorModifier => "color" := c
      }
    }

    override def deserialization(data: SerializeElement): Try[ModifierContainer] = {
      data match {
        case obj: SerializeObject =>
          val modifiers = List.newBuilder[TextModifier]
          obj.get("click").foreach(_.deserialization[ClickEventModifier.type].foreach(modifiers += _))
          obj.get("hover").foreach(_.deserialization[HoverEventModifier].foreach(modifiers += _))
          obj.get("legacy").foreach(_.deserialization[LegacyChatFormattingModifier].foreach(modifiers += _))
          obj.get("decoration").foreach(_.deserialization[DecorationModifier].foreach(modifiers += _))
          obj.get("color").foreach(_.deserialization[ColorModifier].foreach(modifiers += _))
          Try(ModifierContainer(modifiers.result()))
        case o => throw new IllegalArgumentException(s"Failed to deserialize ModifierContainer: expected SerializeObject, but got ${o.getClass.getName}")
      }
    }
  }

  def apply(modifiers: TextModifier*): ModifierContainer = {
    val modList = modifiers.toList
    val unique = modList.reverse.distinctBy(_.getClass).reverse
    if (unique.lastOption.exists(_.isInstanceOf[ColorModifier])) {
      new ModifierContainer(unique)
    } else {
      val (colors, others) = unique.partition(_.isInstanceOf[ColorModifier])
      new ModifierContainer(others ++ colors)
    }
  }

  extension (self: ModifierContainer) {

    def filterNot(pred: TextModifier => Boolean): ModifierContainer = {
      ModifierContainer(self.modifiers.filterNot(pred))
    }

  }

}