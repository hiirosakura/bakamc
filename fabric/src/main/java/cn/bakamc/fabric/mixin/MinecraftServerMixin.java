package cn.bakamc.fabric.mixin;

import cn.bakamc.fabric.chat.FabricMessageHandler;
import cn.bakamc.fabric.config.Config;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
		Config.INSTANCE.init((MinecraftServer) (Object) this);
		FabricMessageHandler.init((MinecraftServer) (Object) this);
	}


	@Inject(method = "saveAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;save(ZZZ)Z", shift = At.Shift.AFTER))
	private void saveEverything(boolean bl, boolean bl2, boolean bl3, CallbackInfoReturnable<Boolean> cir) {
		if (Config.INSTANCE.getNeedSave()) {
			Config.INSTANCE.saveAsync();
		}
	}

	@Inject(method = "shutdown", at = @At("HEAD"))
	private void beforeShutdownServer(CallbackInfo info) {
		Config.INSTANCE.saveAsync();
		FabricMessageHandler.hasHandler(FabricMessageHandler::close);
	}

}
