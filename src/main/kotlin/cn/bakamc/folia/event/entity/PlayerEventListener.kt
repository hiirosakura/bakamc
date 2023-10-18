package cn.bakamc.folia.event.entity

import cn.bakamc.folia.flight_energy.FlightEnergyManager
import cn.bakamc.folia.service.PlayerService
import cn.bakamc.folia.util.logger
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.logging.Level

object PlayerEventListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        runCatching {
            PlayerService.insertOrUpdate(event.player)
            FlightEnergyManager.onPlayerJoin(event.player)
        }.onSuccess {
            logger.info("玩家加入游戏")
        }.onFailure {
            logger.log(Level.WARNING, "数据库错误", it)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent){
        runCatching {
            FlightEnergyManager.onPlayerQuit(event.player)
        }
    }

}