@file:Suppress("FunctionName")

package cn.bakamc.folia.util

import moe.forpleuvoir.nebula.common.color.RGBColor
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.*
import net.minecraft.network.chat.HoverEvent.ItemStackInfo
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import org.bukkit.block.Block
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack
import org.bukkit.entity.Player

fun literalText(content: String = ""): MutableComponent {
    if (content.isEmpty()) return Component.empty()
    return Component.literal(content)
}

fun literalText(content: String = "", style: Style): MutableComponent {
    if (content.isEmpty()) return Component.empty()
    return Component.literal(content).withStyle { style }
}


fun formatText(text: String, style: Style = Style.EMPTY, vararg params: Any): MutableComponent {
    val result = literalText("")
    for ((index, str) in text.split("{}").withIndex()) {
        result.append(literalText(str).withStyle { style })
        if (params.size - index >= 1) result.append(params[index].toText())
    }
    return result
}

fun formatText(text: String, vararg params: Any): MutableComponent {
    val result = literalText("")
    for ((index, str) in text.split("{}").withIndex()) {
        result.append(literalText(str))
        if (params.size - index >= 1) result.append(params[index].toText())
    }
    return result
}

fun CommandSender.sendMessage(message: Component) {
    sendMessage(JSONComponentSerializer.json().deserialize(Component.Serializer.toJson(message)))
}

private fun Any.toText(): Component {
    return when (this) {
        is Component                             -> this

        is String                                -> literalText(this)

        is Block                                 -> wrapInSquareBrackets(literalText(this.blockData.material.name))

        is net.minecraft.world.level.block.Block -> wrapInSquareBrackets(this.name)

        is org.bukkit.inventory.ItemStack        -> CraftItemStack.asNMSCopy(this).getDisplayNameWithCount()

        is ItemStack                             -> this.getDisplayNameWithCount()

        is Player                                -> wrapInSquareBrackets(literalText((this.displayName() as TextComponent).content())) { it.applyFormat(ChatFormatting.AQUA) }

        is ServerPlayer                          -> wrapInSquareBrackets(literalText(this.displayName)) { it.applyFormat(ChatFormatting.AQUA) }

        else                                     -> literalText(this.toString())
    }
}

operator fun MutableComponent.plus(component: Component): MutableComponent {
    return this.append(component)
}

fun wrapInSquareBrackets(text: Component): MutableComponent {
    return ComponentUtils.wrapInSquareBrackets(text)
}

fun wrapInSquareBrackets(text: MutableComponent, style: (Style) -> Style): MutableComponent {
    return ComponentUtils.wrapInSquareBrackets(text.withStyle(style))
}

fun ItemStack.getDisplayNameWithCount(): MutableComponent {
    val mutableText: MutableComponent = Component.empty()
            .append(this.getHoverName())
            .append(if (count > 1) " x${this.count}" else "")
    if (this.hasCustomHoverName()) {
        mutableText.withStyle(ChatFormatting.ITALIC)
    }
    val mutableText1 = wrapInSquareBrackets(mutableText)
    if (!this.isEmpty) {
        mutableText1.withStyle(this.rarity.color).withStyle { style ->
            style.withHoverEvent(
                HoverEvent(HoverEvent.Action.SHOW_ITEM, ItemStackInfo(this))
            )
        }
    }
    return mutableText1
}

fun Style(formatting: ChatFormatting): Style {
    return Style.EMPTY.applyFormat(formatting)
}

fun Style(vararg formatting: ChatFormatting): Style {
    return Style.EMPTY.applyFormats(*formatting)
}

fun Style(color: RGBColor): Style {
    return Style.EMPTY.withColor(color)
}

fun Style.withColor(color: RGBColor): Style {
    return this.withColor(color.rgb)
}


fun Style(hoverEvent: HoverEvent): Style {
    return Style.EMPTY.withHoverEvent(hoverEvent)
}

fun Style(clickEvent: ClickEvent): Style {
    return Style.EMPTY.withClickEvent(clickEvent)
}

fun Style(font: ResourceLocation): Style {
    return Style.EMPTY.withFont(font)
}

fun StyleBold(bold: Boolean = true): Style {
    return Style.EMPTY.withBold(bold)
}

fun StyleItalic(italic: Boolean = true): Style {
    return Style.EMPTY.withItalic(italic)
}

fun StyleUnderlined(underlined: Boolean = true): Style {
    return Style.EMPTY.withUnderlined(underlined)
}

fun StyleStrikethrough(strikethrough: Boolean = true): Style {
    return Style.EMPTY.withStrikethrough(strikethrough)
}

fun StyleObfuscated(obfuscated: Boolean = true): Style {
    return Style.EMPTY.withObfuscated(obfuscated)
}






