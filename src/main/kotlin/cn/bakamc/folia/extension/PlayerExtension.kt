package cn.bakamc.folia.extension

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.db.entity.PlayerVO
import org.bukkit.entity.Player

val Player.VO: PlayerVO
    get() {
        return PlayerVO(this.uuid, this.name)
    }

val Player.uuid: String
    get() = this.uniqueId.toString()

val onlinePlayers: Collection<Player> get() = BakaMCPlugin.insctence.server.onlinePlayers