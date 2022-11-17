package cn.bakamc.fabric.mixin;

import cn.bakamc.common.chat.MessageHandler;
import cn.bakamc.common.town.TownManager;
import cn.bakamc.fabric.chat.FabricMessageHandler;
import cn.bakamc.fabric.config.FabricCommonConfig;
import cn.bakamc.fabric.config.FabricConfig;
import cn.bakamc.fabric.town.FabricTownManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

/**
 * 项目名 bakamc
 * <p>
 * 包名 cn.bakamc.fabric.mixin
 * <p>
 * 文件名 MinecraftServerMixin
 * <p>
 * 创建时间 2022/9/6 14:21
 *
 * @author forpleuvoir
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

	@Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setupServer()Z"))
	private void beforeSetupServer(CallbackInfo info) {
		CompletableFuture.runAsync(() -> {
			FabricConfig.INSTANCE.init((MinecraftServer) (Object) this);
			FabricCommonConfig.init(FabricConfig.Server.INSTANCE);
			FabricMessageHandler.init((MinecraftServer) (Object) this, FabricCommonConfig.getINSTANCE());
			FabricTownManager.init(FabricConfig.Server.INSTANCE);
		});
	}


	@Inject(method = "saveAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;save(ZZZ)Z", shift = At.Shift.AFTER))
	private void saveEverything(boolean bl, boolean bl2, boolean bl3, CallbackInfoReturnable<Boolean> cir) {
		if (FabricConfig.INSTANCE.getNeedSave()) {
			FabricConfig.INSTANCE.saveAsync();
		}
	}

	@Inject(method = "shutdown", at = @At("HEAD"))
	private void beforeShutdownServer(CallbackInfo info) {
		FabricConfig.INSTANCE.saveAsync();
		FabricMessageHandler.hasHandler(MessageHandler::close);
		FabricTownManager.hasManager(TownManager::close);
	}

}
