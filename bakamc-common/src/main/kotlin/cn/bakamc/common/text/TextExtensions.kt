package cn.bakamc.common.text

import net.kyori.adventure.extra.kotlin.text
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.Style

fun literal(str: String): TextComponent {
    return Component.text(str)
}

fun literal(str: String, style: Style): TextComponent {
    return Component.text(str, style)
}

/**
 * 将当前的可变文本对象转换为一个扁平化的、不可变的文本列表
 *
 * 此方法的主要目的是将一个可能包含多个子文本的复杂文本对象，
 * 转换为一个简单的文本列表，其中每个子文本都被视为独立的文本对象
 * 这对于文本处理和渲染非常有用，因为它允许统一处理所有子文本，
 * 而不必单独处理每个子文本的样式和内容
 *
 * @return 返回一个包含扁平化文本的列表，每个文本都是不可变的[TextComponent]实例
 */
fun TextComponent.flat(): List<Component> {
    // 创建一个列表构建器，用于收集扁平化的文本对象
    return buildList {
        // 首先添加当前文本对象的内容和样式作为一个新的不可变文本对象
        add(text {
            content(this@flat.content())
            style(this@flat.style())
        })

        // 遍历当前文本对象的所有子文本
        this@flat.children().forEach { text ->
            // 如果子文本也是可变的，则递归调用flat方法将其扁平化，并添加到列表中
            if (text is TextComponent) {
                addAll(text.flat())
            } else {
                // 如果子文本已经是不可变的，则直接添加到列表中
                add(text)
            }
        }
    }
}