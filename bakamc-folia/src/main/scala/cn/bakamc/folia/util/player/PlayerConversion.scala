package cn.bakamc.folia.util.player

import net.minecraft.server.level.ServerPlayer
import org.bukkit.entity.Player

object PlayerConversion {

  given Conversion[Player, ServerPlayer] = _.toVanilla

  given Conversion[ServerPlayer, Player] = _.toBukkit

  extension (player: Player) {
    def toVanilla: ServerPlayer = {
      player.asInstanceOf[ServerPlayer]
    }
  }

  extension (player: ServerPlayer) {
    def toBukkit: Player = {
      player.getBukkitEntity
    }
  }

}
