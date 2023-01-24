package cn.bakamc.fabric

import cn.bakamc.common.BakaMcApp
import cn.bakamc.common.town.TownHandler
import cn.bakamc.fabric.chat.FabricMessageHandler
import cn.bakamc.fabric.command.Commands
import cn.bakamc.fabric.config.FabricCommonConfig
import cn.bakamc.fabric.config.FabricCommonConfig.Companion.INSTANCE
import cn.bakamc.fabric.config.FabricConfig
import cn.bakamc.fabric.config.FabricConfig.Server
import cn.bakamc.fabric.player.FabricPlayerHandler
import cn.bakamc.fabric.town.FabricTownHandler
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.server.MinecraftServer
import java.util.concurrent.CompletableFuture

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.fabric

 * 文件名 BakaMC

 * 创建时间 2022/9/6 13:07

 * @author forpleuvoir

 */
object BakaMC : ModInitializer, BakaMcApp<MinecraftServer> {

	const val ID = "bakamc"
	const val NAME = "BakaMC"

	override fun onInitialize() {
		CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess ->
			Commands.register(dispatcher)
		}
	}

	override fun init(server: MinecraftServer) {
		CompletableFuture.runAsync {
			FabricConfig.init(server)
			FabricCommonConfig.init(Server)
			FabricMessageHandler.init(Server, INSTANCE, server)
			FabricTownHandler.init(Server)
			FabricPlayerHandler.init(Server, INSTANCE, server)
		}
	}

	override fun reload(server: MinecraftServer) {
		FabricConfig.load()
		FabricCommonConfig.init(Server)
		FabricMessageHandler.hasHandler { it.close() }
		FabricTownHandler.hasHandler { it.close() }
		FabricPlayerHandler.hasHandler { it.close() }
		FabricMessageHandler.init(Server, INSTANCE, server)
		FabricTownHandler.init(Server)
		FabricPlayerHandler.init(Server, INSTANCE, server)
	}

	override fun close(server: MinecraftServer) {
		FabricConfig.saveAsync()
		FabricMessageHandler.hasHandler { it.close() }
		FabricTownHandler.hasHandler(TownHandler::close)
		FabricPlayerHandler.hasHandler { it.close() }
	}


}