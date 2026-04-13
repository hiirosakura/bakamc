package cn.bakamc.common.inlinestyletext.modifier

import cn.bakamc.common.extension.flat
import cn.bakamc.common.inlinestyletext.TextModifier
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.util.primitive.CoerceInExtension.clamp
import moe.forpleuvoir.nebula.serialization.codec.{Codec, given_Codec_Boolean}
import net.kyori.adventure.text.format.{ShadowColor, TextColor}
import net.kyori.adventure.text.{Component, TextComponent}

import java.text.BreakIterator
import java.util.Locale
import scala.collection.mutable

case class ColorModifier(
  shadow: Boolean,
  gradient: Boolean
) extends TextModifier derives Codec {

  override def modify(exp: String): Option[Component => Component] = {
    val shouldApplyShadow = exp.startsWith("s") && this.shadow
    val cleanExp = if (exp.startsWith("s")) exp.substring(1) else exp

    if (cleanExp == "#null") {
      return Some { text =>
        if (shouldApplyShadow) {
          text.style(text.style().shadowColor(null))
        } else
          text.color(null)
      }
    } else if (exp == "s#none") {
      return if (shouldApplyShadow) {
        Some { text =>
          text.style(text.style().shadowColor(ShadowColor.none()))
        }
      } else None
    }

    parseRGBColor(cleanExp, shouldApplyShadow)
      .orElse(parseHSVColor(cleanExp, shouldApplyShadow))
  }

  private def singleColorModifier(color: Color, shadow: Boolean): Option[Component => Component] = Some { text =>
    var style = text.style()
    if (shadow) {
      style = style.shadowColor(ShadowColor.shadowColor(color.argb))
    } else {
      style = style.color(TextColor.color(color.argb))
    }
    text.style(style)
  }

  private def parseRGBColor(exp: String, shadow: Boolean): Option[Component => Component] = {
    try singleColorModifier(Color.fromHexString(exp), shadow)
    catch case _: Throwable => if (gradient) parseRGBGradient(exp, shadow) else None
  }

  private def parseRGBGradient(exp: String, shadow: Boolean): Option[Component => Component] = {
    val colors = try exp.split("->").map(Color.fromHexString)
    catch case _: Throwable => return None
    if (colors.length < 2) return None
    Some {
      case t: TextComponent =>
        multiGradientText(t, colors.toList, shadow, false)
      case other => other
    }
  }

  private def parseHSVColor(exp: String, shadow: Boolean): Option[Component => Component] = {
    try singleColorModifier(parseHSV(exp).get, shadow)
    catch case _: Throwable => if (gradient) parseHSVGradient(exp, shadow) else None
  }

  private def parseHSVGradient(exp: String, shadow: Boolean): Option[Component => Component] = {
    val colors = try exp.split("->").map(e => parseHSV(e).get)
    catch case _: Throwable => return None
    if (colors.length < 2) return None
    Some {
      case t: TextComponent =>
        multiGradientText(t, colors.toList, shadow, true)
      case other => other
    }
  }


  private def multiGradientText(text: TextComponent, colors: List[Color], shadow: Boolean, isHsv: Boolean): Component = {
    val texts = splitText(text)
    if (texts.isEmpty) return text
    val total = texts.size

    val coloredText = texts.zipWithIndex.map { case (comp, i) =>
      val t = if (total <= 1) 0f else i.toFloat / (total - 1)

      val segment = (t * (colors.size - 1)).toInt.clamp(0, colors.size - 2)
      val localT = (t * (colors.size - 1)) - segment // 该段内的比例

      val startColor = colors(segment)
      val endColor = colors(segment + 1)

      val interpolated = if (isHsv)
        startColor.hsvLerp(endColor, localT, true)
      else
        startColor.lerp(endColor, localT, true)

      val style = if (shadow) {
        comp.style().shadowColor(ShadowColor.shadowColor(interpolated.argb))
      } else {
        comp.style().color(TextColor.color(interpolated.argb))
      }
      comp.style(style)
    }
    import scala.jdk.CollectionConverters.*
    Component.text("")
      .children(coloredText.asJava)
  }


  private def gradientColor(start: Color, end: Color, steps: Int, alpha: Boolean, isHsv: Boolean)(block: Color => Unit): Unit = {
    require(steps > 0, "steps must be greater than 0")
    if (steps == 1) {
      block(start)
    } else {
      for (i <- 0 until steps) {

        val color = if (isHsv)
          start.hsvLerp(end, (i * (1f / (steps - 1f))).clamp(0f, 1f), alpha)
        else
          start.lerp(end, (i * (1f / (steps - 1f))).clamp(0f, 1f), alpha)

        block(color)
      }
    }
  }


  private val breakIterator = ThreadLocal.withInitial(() => BreakIterator.getCharacterInstance(Locale.ROOT))

  private def splitText(text: Component): List[Component] = {
    val result = mutable.ListBuffer.empty[Component]
    text.flat().foreach { case t: TextComponent =>
      val content = t.content()
      if (content.nonEmpty) {
        val it = breakIterator.get()
        it.setText(content)
        var start = it.first()
        var end = it.next()
        while (end != BreakIterator.DONE) {
          val cluster = content.substring(start, end)
          if (cluster.nonEmpty) {
            result += Component.text(cluster).style(t.style())
          }
          start = end
          end = it.next()
        }
      }
    }
    result.toList
  }

  private final val HSV_PATTERN = """\[(\d+(?:\.\d+)?)\s+(\d+(?:\.\d+)?)\s+(\d+(?:\.\d+)?)]""".r

  private def parseHSV(exp: String): Option[Color] = exp match {
    case HSV_PATTERN(h, s, v) =>
      val hue = h.toFloat
      val sat = s.toFloat
      val value = v.toFloat
      if (hue <= 360 && sat <= 100f && value <= 100f)
        Some(Color.fromHSV(hue, sat, value))
      else None
    case _ => None
  }
}
