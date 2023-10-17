package cn.bakamc.folia.command

import cn.bakamc.folia.flight_energy.FlightEnergyManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object FlyCommand : CommandExecutor {

    const val CMD = "baka-fly"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§c只有玩家可以使用此命令！")
            return false
        }
        if (args.isEmpty()) {
            FlightEnergyManager.apply {
                return if (sender.energy > 0) {
                    sender.allowFlight = !sender.allowFlight
                    sender.sendMessage("§a飞行状态已切换:[${if(sender.allowFlight) "§a开启" else "§c关闭"}]}]")
                    true
                } else {
                    sender.sendMessage("§c你的飞行能量不足")
                    false
                }
            }
        } else if (args.size == 1) {
//            sender.inventory.
        }
        return false
    }


}