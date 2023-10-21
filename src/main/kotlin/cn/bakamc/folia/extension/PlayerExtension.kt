package cn.bakamc.folia.extension

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.db.table.PlayerInfo
import org.bukkit.entity.Player

val Player.info: PlayerInfo
    get() {
        return PlayerInfo {
            uuid = this@info.uuid
            name = this@info.name
        }
    }

val Player.uuid: String
    get() = this.uniqueId.toString()

val onlinePlayers: Collection<Player> get() = BakaMCPlugin.instance.server.onlinePlayers