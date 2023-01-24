package cn.bakamc.fabric.player

import cn.bakamc.common.config.common.CommonConfig
import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.common.config.common.TextConfig
import cn.bakamc.common.player.PlayerHandler
import cn.bakamc.fabric.common.FabricPlatform
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import java.util.function.Consumer

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.fabric.player

 * 文件名 FabricPlayerHandler

 * 创建时间 2022/11/26 12:58

 * @author forpleuvoir

 */
class FabricPlayerHandler(config: ServerConfig, private val commonConfig: CommonConfig, server: MinecraftServer) :
	PlayerHandler<MutableText, ServerPlayerEntity, MinecraftServer>(config, server), FabricPlatform {

	companion object {
		@JvmStatic
		lateinit var INSTANCE: PlayerHandler<MutableText, ServerPlayerEntity, MinecraftServer>
			private set

		@JvmStatic
		fun hasHandler(action: Consumer<PlayerHandler<MutableText, ServerPlayerEntity, MinecraftServer>>) {
			if (this::INSTANCE.isInitialized) {
				if (INSTANCE.isConnected()) action.accept(INSTANCE)
			}
		}

		@JvmStatic
		fun init(
			serverConfig: ServerConfig,
			commonConfig: CommonConfig,
			server: MinecraftServer
		): PlayerHandler<MutableText, ServerPlayerEntity, MinecraftServer> {
			INSTANCE = FabricPlayerHandler(serverConfig, commonConfig, server)
			INSTANCE.connect()
			return INSTANCE
		}
	}

	override val textConfig: TextConfig
		get() = commonConfig.textConfig


}