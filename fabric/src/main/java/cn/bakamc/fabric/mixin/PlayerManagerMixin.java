package cn.bakamc.fabric.mixin;

import cn.bakamc.fabric.player.FabricPlayerHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 项目名 bakamc
 * <p>
 * 包名 cn.bakamc.fabric.mixin
 * <p>
 * 文件名 PlayerManagerMixin
 * <p>
 * 创建时间 2022/11/26 14:48
 *
 * @author forpleuvoir
 */
@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

	@Inject(method = "onPlayerConnect", at = @At("RETURN"))
	public void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo callbackInfo) {
		FabricPlayerHandler.hasHandler(manager -> {
			manager.onPlayerJoin(player);
		});
	}

	@Inject(method = "remove", at = @At("RETURN"))
	public void onPlayerLeft(ServerPlayerEntity player, CallbackInfo callbackInfo) {
		FabricPlayerHandler.hasHandler(manager -> {
			manager.onPlayerLeft(player);
		});
	}

}
