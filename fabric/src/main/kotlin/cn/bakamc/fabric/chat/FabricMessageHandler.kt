package cn.bakamc.fabric.chat

import cn.bakamc.common.chat.AbstractMessageHandler
import cn.bakamc.common.chat.MessageHandler
import cn.bakamc.common.config.common.CommonConfig
import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.common.config.common.TextConfig
import cn.bakamc.fabric.common.FabricPlatform
import net.minecraft.item.ItemStack
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.*
import net.minecraft.text.HoverEvent.Action
import net.minecraft.text.HoverEvent.ItemStackContent
import net.minecraft.util.Formatting.ITALIC
import java.util.function.Consumer

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.fabric.chat

 * 文件名 FabricMessageHandler

 * 创建时间 2022/9/6 13:29

 * @author forpleuvoir

 */
class FabricMessageHandler(config: ServerConfig, override val commonConfig: CommonConfig, override val server: MinecraftServer) :
	AbstractMessageHandler<MutableText, ServerPlayerEntity, MinecraftServer>(config, commonConfig), FabricPlatform {

	companion object {
		@JvmStatic
		lateinit var INSTANCE: MessageHandler<MutableText, ServerPlayerEntity, MinecraftServer>
			private set

		@JvmStatic
		fun hasHandler(action: Consumer<MessageHandler<MutableText, ServerPlayerEntity, MinecraftServer>>) {
			if (this::INSTANCE.isInitialized) {
				action.accept(INSTANCE)
			}
		}

		@JvmStatic
		fun init(
			serverConfig: ServerConfig,
			commonConfig: CommonConfig,
			server: MinecraftServer
		): MessageHandler<MutableText, ServerPlayerEntity, MinecraftServer> {
			INSTANCE = FabricMessageHandler(serverConfig, commonConfig, server)
			INSTANCE.connect()
			return INSTANCE
		}
	}

	override val textConfig: TextConfig
		get() = this.commonConfig.textConfig

	override fun ServerPlayerEntity.getItemText(index: Int): MutableText {
		val item = when (index) {
			-2   -> this.offHandStack
			-1   -> this.mainHandStack
			else -> this.inventory.getStack(index)
		}
		if (item.isEmpty) {
			return if (index == -2) "%o".text else if (index == -1) "%i".text else "%$index".text
		}
		return item.getHoverableText()
	}

	private fun ItemStack.getHoverableText(): MutableText {
		val mutableText: MutableText = LiteralText("").append(this.name)
		val count = this.count
		if (count > 1) mutableText.append(" x$count")
		if (this.hasCustomName()) {
			mutableText.formatted(ITALIC)
		}
		val mutableText2 = Texts.bracketed(mutableText)
		if (!this.isEmpty) {
			mutableText2.formatted(this.rarity.formatting).styled { style: Style ->
				style.withHoverEvent(
					HoverEvent(Action.SHOW_ITEM, ItemStackContent(this))
				)
			}
		}
		return mutableText2
	}

}