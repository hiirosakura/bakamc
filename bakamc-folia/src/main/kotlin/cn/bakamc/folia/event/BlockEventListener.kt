package cn.bakamc.folia.event

import cn.bakamc.common.text.bakatext.BakaText
import cn.bakamc.common.text.bakatext.modifier.ColorModifier
import cn.bakamc.common.text.bakatext.modifier.DecorationModifier
import cn.bakamc.common.text.bakatext.modifier.LegacyChatFormattingModifier
import cn.bakamc.folia.config.MiscConfig.ANVIL_RENAME_DECORATION_MAPPING
import cn.bakamc.folia.config.MiscConfig.ANVIL_RENAME_LEGACY_FORMAT_CHARS
import cn.bakamc.folia.config.MiscConfig.ENABLE_ANVIL_CUSTOM_RENAME
import cn.bakamc.folia.util.debugInfo
import cn.bakamc.folia.util.logger
import cn.bakamc.folia.util.plainText
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemStack

object BlockEventListener : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onAnvilRename(event: PrepareAnvilEvent) {
        if (ENABLE_ANVIL_CUSTOM_RENAME)
            event.result?.let { itemStack ->
                event.inventory.result?.let { result ->
                    val renameText = result.itemMeta.displayName()?.plainText ?: ""
                    if (!BakaText.regex.containsMatchIn(renameText)) return
                    val text = runCatching {
                        BakaText.parse(
                            renameText, listOf(
                                DecorationModifier(ANVIL_RENAME_DECORATION_MAPPING),
                                ColorModifier,
                                LegacyChatFormattingModifier(formatChars = ANVIL_RENAME_LEGACY_FORMAT_CHARS.toSet())
                            )
                        )
                    }.onFailure {
                        logger.debugInfo("物品重命名解析失败: ${event.inventory.firstItem?.displayName()?.plainText} -> $renameText")
                        logger.warn(it.message, it)
                    }.onSuccess {
                        logger.debugInfo("物品重命名: ${event.inventory.firstItem?.displayName()?.plainText} -> $renameText")
                    }
                    val new = ItemStack(itemStack)
                    new.editMeta { it.displayName(text.getOrDefault(result.itemMeta.displayName())) }
                    event.result = new
                    event.inventory.result = new
                }
            }
    }

}