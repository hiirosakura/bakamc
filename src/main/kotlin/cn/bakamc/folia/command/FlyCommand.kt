package cn.bakamc.folia.command

import cn.bakamc.folia.config.Configs.FlightEnergy.MAX_COST
import cn.bakamc.folia.config.Configs.FlightEnergy.MONEY_ITEM
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import cn.bakamc.folia.item.SpecialItem
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftInventoryPlayer
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack
import org.bukkit.entity.Player

object FlyCommand : CommandExecutor, TabCompleter {

    const val CMD = "baka-fly"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§c只有玩家可以使用此命令！")
            return false
        }
        if (args.isNullOrEmpty()) {
            FlightEnergyManager.apply {
                return if (sender.energy > 0) {
                    sender.allowFlight = !sender.allowFlight
                    sender.sendMessage("§a飞行状态已切换:[${if (sender.allowFlight) "§a开启" else "§c关闭"}]}]")
                    true
                } else {
                    sender.sendMessage("§c你的飞行能量不足,输入指令 §a/baka-fly <货币类型> <使用数量> §c购买")
                    false
                }
            }
        } else if (args.size == 1) {
            sender.sendMessage("§6每个${args[0]}可以兑换${MONEY_ITEM[args[0]]}飞行能量")
            return false
        } else if (args.size == 2) {
            val key = args[0]
            var count = args[1].toInt()

            if (SpecialItem.specialItems.containsKey(key) && MONEY_ITEM.containsKey(key)) {

                sender as CraftPlayer
                val inventory = sender.inventory as CraftInventoryPlayer

                //纪录扣费动作
                val actions = mutableListOf<() -> Unit>()

                inventory.filter {//过滤出对应的货币
                    val stack = CraftItemStack.asNMSCopy(it)

                    SpecialItem.specialItems[key]!!.isMatch(stack)
                }.forEach { stack ->
                    val temp = count.coerceAtMost(stack.amount)
                    count -= temp

                    actions.add {
                        stack.amount -= temp
                    }

                }

                if (count == 0) {//有足够的货币
                    val energy = MONEY_ITEM[key]!! * args[1].toInt()
                    FlightEnergyManager.apply {
                        return if (energy + sender.energy > MAX_COST) {//超出了能量上限
                            sender.sendMessage("§c超出了能量上限[最大值:$MAX_COST,充值后会超出:${energy + sender.energy - MAX_COST}]")
                            false
                        } else {//购买成功
                            sender.energy += energy
                            //执行扣费操作
                            actions.forEach { it.invoke() }
                            sender.sendMessage("§a成功购买§e[${key}*${args[1].toInt()}]§a货币的能量§e($energy)§a,当前能量§e[${sender.energy}]")
                            true
                        }
                    }
                } else {//没有足够的货币
                    sender.sendMessage("§c你所拥有的对应货币§a[${args[0]}]§a数量不足(需要§a[${args[1].toInt()}]§c个,在背包中找到§6[${args[1].toInt() - count}])")
                }
            }
            sender.sendMessage("§c无效的货币[${key}]类型!")
            return false
        }
        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String>? {
        if (sender !is Player) {
            return mutableListOf("§c只有玩家可以使用此命令！")
        }
        if (args?.size == 1) {
            return SpecialItem.specialItems.keys.toMutableList()
        } else if (args?.size == 2) {
            val energy = MONEY_ITEM[args[0]]
            return mutableListOf("§6每个${args[0]}可以兑换${energy}飞行能量")
        }
        return null
    }


}