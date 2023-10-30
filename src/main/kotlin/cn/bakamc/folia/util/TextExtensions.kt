package cn.bakamc.folia.util

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.HoverEvent.ItemStackInfo
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.ItemStack

fun literalText(content: String = ""): MutableComponent {
    if (content.isEmpty()) return Component.empty()
    return Component.literal(content)
}

fun wrapInSquareBrackets(text: Component): MutableComponent {
    return ComponentUtils.wrapInSquareBrackets(text)
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