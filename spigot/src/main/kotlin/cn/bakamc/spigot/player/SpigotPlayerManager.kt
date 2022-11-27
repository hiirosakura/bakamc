package cn.bakamc.spigot.player

import cn.bakamc.common.config.common.CommonConfig
import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.common.config.common.TextConfig
import cn.bakamc.common.player.PlayerManager
import cn.bakamc.spigot.common.SpigotPlatform
import net.minecraft.network.chat.IChatMutableComponent
import org.bukkit.Server
import org.bukkit.entity.Player
import java.util.function.Consumer

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot.player

 * 文件名 SpigotPlayerManager

 * 创建时间 2022/11/26 14:19

 * @author forpleuvoir

 */
class SpigotPlayerManager(config: ServerConfig, private val commonConfig: CommonConfig, server: Server) :
	PlayerManager<IChatMutableComponent, Player, Server>(config, server), SpigotPlatform {
	companion object {
		@JvmStatic
		lateinit var INSTANCE: PlayerManager<IChatMutableComponent, Player, Server>
			private set

		@JvmStatic
		fun hasManager(action: Consumer<PlayerManager<IChatMutableComponent, Player, Server>>) {
			if (this::INSTANCE.isInitialized) {
				action.accept(INSTANCE)
			}
		}

		@JvmStatic
		fun init(
			serverConfig: ServerConfig,
			commonConfig: CommonConfig,
			server: Server
		): PlayerManager<IChatMutableComponent, Player, Server> {
			INSTANCE = SpigotPlayerManager(serverConfig, commonConfig, server)
			INSTANCE.connect()
			return INSTANCE
		}
	}

	override val textConfig: TextConfig
		get() = commonConfig.textConfig

}