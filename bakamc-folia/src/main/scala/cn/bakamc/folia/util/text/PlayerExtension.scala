package cn.bakamc.folia.util.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player

object PlayerExtension {

  private val nameColor = TextColor.color(0x55FFFF)

  extension (player: Player) {

    def nameAsComponent: Component = {
      player.displayName()
        .color(nameColor)
        //TODO 或许以后还有别的需求
        .wrapInSquareBrackets
    }

  }


}
