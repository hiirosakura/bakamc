package cn.bakamc.fabric.mixin;

import cn.bakamc.fabric.chat.FabricMessageHandler;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 项目名 bakamc
 * <p>
 * 包名 cn.bakamc.fabric.mixin
 * <p>
 * 文件名 ServerPlayNetworkHandlerMixin
 * <p>
 * 创建时间 2022/9/6 17:22
 *
 * @author forpleuvoir
 */
@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

	private static final Logger log = LoggerFactory.getLogger("(BakaMC)messageHandler");

	@Shadow
	public ServerPlayerEntity player;

	@Inject(method = "handleMessage", at = @At("HEAD"), cancellable = true)
	public void handleMessage(TextStream.Message arg, CallbackInfo ci) {
		if (!arg.getRaw().startsWith("/"))
			FabricMessageHandler.hasHandler(handler -> {
				try {
					handler.sendChatMessage(player, arg.getRaw());
					ci.cancel();
				} catch (Exception e) {
					log.error("消息发送失败", e);
				}
			});
	}
}
