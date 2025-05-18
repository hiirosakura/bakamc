package cn.bakamc.proxy.event.velocity

import cn.bakamc.common.text.bakatext.BakaText
import cn.bakamc.proxy.BakamcProxyInstance
import cn.bakamc.proxy.config.MiscConfig.PLAYER_JOIN_MESSAGE
import cn.bakamc.proxy.config.MiscConfig.PLAYER_QUIT_MESSAGE
import cn.bakamc.proxy.feature.ip_restrict.IpRestrictor
import cn.bakamc.proxy.util.Utils
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import kotlin.time.Duration.Companion.milliseconds
import cn.bakamc.proxy.config.IpRestrictConfig.ENABLED as ipRestrictEnabled

object PlayerEventListener {

    @Subscribe
    fun onPlayerJoin(event: ServerPreConnectEvent) {
//        if (whiteListEnabled) {
//            WhiteListManager.onPlayerJoin(event)
//        }

        if (ipRestrictEnabled) {
            IpRestrictor.onPlayerConnect(event.player)
        }
    }

    @Subscribe
    fun onServerConnectedEvent(event: ServerConnectedEvent) {
        val player = event.player
        val currentServer = event.server

        BakamcProxyInstance.runDelayed(500.milliseconds) {
            BakamcProxyInstance.server.allServers.forEach { server ->
                server.sendMessage(BakaText.parse(Utils.replace(PLAYER_JOIN_MESSAGE, currentServer, player)))
            }
        }
    }

    @Subscribe
    fun onPlayerQuit(event: DisconnectEvent) {
        IpRestrictor.onPlayerDisconnect(event.player)

        val player = event.player
        BakamcProxyInstance.runTask {
            BakamcProxyInstance.server.allServers.forEach { server ->
                server.sendMessage(BakaText.parse(Utils.replace(PLAYER_QUIT_MESSAGE, server, player)))
            }
        }
    }

}