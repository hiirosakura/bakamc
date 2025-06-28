package cn.bakamc.common.text.bakatext.modifier

import cn.bakamc.common.util.gradient
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.HSVColor
import net.kyori.adventure.extra.kotlin.style
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import java.text.BreakIterator
import java.util.*

object ColorModifier : Modifier {
    override fun modifier(exp: String): ((TextComponent) -> TextComponent)? {
        return parseColor(exp)
    }

    private fun parseColor(exp: String): ((TextComponent) -> TextComponent)? {
        parseRGBColor(exp)?.let {
            return it
        }
        parseHSVColor(exp)?.let {
            return it
        }
        return null
    }

    private fun parseRGBColor(exp: String): ((TextComponent) -> TextComponent)? {
        //&{#FF66CC}
        if (exp.matches(Regex("#[0-9A-Fa-f]{6}")) && exp.length == 7) {
            return { text -> text.color(TextColor.color(Color(exp).rgb)) }
        }
        //&{#FF66CC->#FF88BB}
        if (exp.matches(Regex("#[0-9A-Fa-f]{6}->#[0-9A-Fa-f]{6}"))) {
            val (sStart, sEnd) = exp.split("->")
            val (start, end) = Color(sStart) to Color(sEnd)
            return { text ->
                gradientText(text.content(), start, end)
            }
        }
        return null
    }

    private fun parseHSVColor(exp: String): ((TextComponent) -> TextComponent)? {
        //&{[360 100 20]}
        if (exp.matches(Regex("\\[((360|3[0-5][0-9]|2\\d{2}|1\\d{2}|\\d{1,2})(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?)]"))) {
            val hsv = exp.substring(1, exp.length - 1).split(' ').map { it.toFloat() }
            return { text -> text.color { HSVColor(hsv[0], hsv[1] / 100f, hsv[2] / 100f).rgb } }
        }
        //&{[360 99.6 20]->[360 20 100]}
        if (exp.matches(Regex("\\[((360|3[0-5][0-9]|2\\d{2}|1\\d{2}|\\d{1,2})(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?)]->\\[((360|3[0-5][0-9]|2\\d{2}|1\\d{2}|\\d{1,2})(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?) \\s*((100|[1-9]?\\d)(\\.\\d+)?)]"))) {
            val (fStart, fEnd) = exp.split("->").map { it -> it.substring(1, it.length - 1).split(" ").map { it.toFloat() } }
            val (start, end) = HSVColor(fStart[0], fStart[1] / 100, fStart[2] / 100) to HSVColor(fEnd[0], fEnd[1] / 100, fEnd[2] / 100)
            return { text ->
                gradientText(text.content(), start, end)
            }
        }
        return null
    }

    private fun <C : ARGBColor> gradientText(content: String, start: C, end: C): TextComponent {
        return text { build ->
            if (content.isEmpty()) return@text
            val texts = splitText(content)
            start.gradient(end, texts.size).forEachIndexed { index, color ->
                build.append(text(texts[index], style {
                    color(TextColor.color(color.rgb))
                }))
            }
        }
    }

    private fun splitText(text: String): List<String> = buildList {
        val it: BreakIterator = BreakIterator.getCharacterInstance(Locale.US)
        it.setText(text)
        var start = it.first()
        var end = it.next()

        while (end != BreakIterator.DONE) {
            val emoji = text.substring(start, end)
            add(emoji)

            start = end
            end = it.next()
        }
    }


}