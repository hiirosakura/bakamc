package cn.bakamc.fabric.player

import cn.bakamc.common.config.common.CommonConfig
import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.common.config.common.TextConfig
import cn.bakamc.common.player.PlayerManager
import cn.bakamc.fabric.common.FabricPlatform
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import java.util.function.Consumer

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.fabric.player

 * 文件名 FabricPlayerManager

 * 创建时间 2022/11/26 12:58

 * @author forpleuvoir

 */
class FabricPlayerManager(config: ServerConfig, private val commonConfig: CommonConfig, server: MinecraftServer) :
	PlayerManager<MutableText, ServerPlayerEntity, MinecraftServer>(config, server), FabricPlatform {

	companion object {
		@JvmStatic
		lateinit var INSTANCE: PlayerManager<MutableText, ServerPlayerEntity, MinecraftServer>
			private set

		@JvmStatic
		fun hasManager(action: Consumer<PlayerManager<MutableText, ServerPlayerEntity, MinecraftServer>>) {
			if (this::INSTANCE.isInitialized) {
				action.accept(INSTANCE)
			}
		}

		@JvmStatic
		fun init(
			serverConfig: ServerConfig,
			commonConfig: CommonConfig,
			server: MinecraftServer
		): PlayerManager<MutableText, ServerPlayerEntity, MinecraftServer> {
			INSTANCE = FabricPlayerManager(serverConfig, commonConfig, server)
			INSTANCE.connect()
			return INSTANCE
		}
	}

	override val textConfig: TextConfig
		get() = commonConfig.textConfig


}