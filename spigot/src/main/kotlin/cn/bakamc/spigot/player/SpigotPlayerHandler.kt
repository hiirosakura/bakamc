package cn.bakamc.spigot.player

import cn.bakamc.common.config.common.CommonConfig
import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.common.config.common.TextConfig
import cn.bakamc.common.player.PlayerHandler
import cn.bakamc.spigot.common.SpigotPlatform
import net.minecraft.network.chat.IChatMutableComponent
import org.bukkit.Server
import org.bukkit.entity.Player
import java.util.function.Consumer

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot.player

 * 文件名 SpigotPlayerHandler

 * 创建时间 2022/11/26 14:19

 * @author forpleuvoir

 */
class SpigotPlayerHandler(config: ServerConfig, private val commonConfig: CommonConfig, server: Server) :
	PlayerHandler<IChatMutableComponent, Player, Server>(config, server), SpigotPlatform {
	companion object {
		@JvmStatic
		lateinit var INSTANCE: PlayerHandler<IChatMutableComponent, Player, Server>
			private set

		@JvmStatic
		fun hasHandler(action: Consumer<PlayerHandler<IChatMutableComponent, Player, Server>>) {
			if (this::INSTANCE.isInitialized) {
				if (INSTANCE.isConnected()) action.accept(INSTANCE)
			}
		}

		@JvmStatic
		fun init(
			serverConfig: ServerConfig,
			commonConfig: CommonConfig,
			server: Server
		): PlayerHandler<IChatMutableComponent, Player, Server> {
			INSTANCE = SpigotPlayerHandler(serverConfig, commonConfig, server)
			INSTANCE.connect()
			return INSTANCE
		}
	}

	override val textConfig: TextConfig
		get() = commonConfig.textConfig

}