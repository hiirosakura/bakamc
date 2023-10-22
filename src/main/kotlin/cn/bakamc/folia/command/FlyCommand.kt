package cn.bakamc.folia.command

import cn.bakamc.folia.config.Configs.FlightEnergy.MAX_ENERGY
import cn.bakamc.folia.config.Configs.FlightEnergy.MONEY_ITEM
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import cn.bakamc.folia.item.SpecialItemManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftInventoryPlayer
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack
import org.bukkit.entity.Player

object FlyCommand : BakaCommand {

    override val subCommand: String = "fly"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§c只有玩家可以使用此命令！")
            return false
        }
        if (args.isNullOrEmpty()) {
            FlightEnergyManager.apply {
                return if (sender.energy > 0) {
                    toggleFly(sender)
                    sender.sendMessage("§a飞行状态已切换:[${if (sender.allowFlight) "§a开启" else "§c关闭"}§a]")
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

            val moneyItems = SpecialItemManager.specifyType(MONEY_ITEM.keys)

            if (moneyItems.containsKey(key)) {

                sender as CraftPlayer
                val inventory = sender.inventory as CraftInventoryPlayer

                //纪录扣费动作
                val actions = mutableListOf<() -> Unit>()

                inventory.filter {//过滤出对应的货币
                    val stack = CraftItemStack.asNMSCopy(it)
                    if (!stack.isEmpty)
                        TODO()
                    else false
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
                        return if (energy + sender.energy > MAX_ENERGY) {//超出了能量上限
                            sender.sendMessage("§c超出了能量上限[最大值:$MAX_ENERGY,充值后会超出:${energy + sender.energy - MAX_ENERGY}]")
                            false
                        } else {//购买成功
                            sender.energy += energy
                            //执行扣费操作
                            actions.forEach { it.invoke() }
                            sender.sendMessage("§a成功购买§e[${key}*${args[1].toInt()}]§a货币的能量§e[$energy]§a,当前能量§e[${sender.energy}]")
                            true
                        }
                    }
                } else {//没有足够的货币
                    sender.sendMessage("§c你所拥有的对应货币§a[${args[0]}]§c数量不足§e(需要§6[${args[1].toInt()}]§e个,在背包中找到§c[${args[1].toInt() - count}]§e)")
                    return false
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
            return MONEY_ITEM.keys.toMutableList()
        } else if (args?.size == 2) {
            val energy = MONEY_ITEM[args[0]]
            val count = args[1].toIntOrNull() ?: 0
            return if (energy != null)
                mutableListOf("§6每个${args[0]}可以兑换§a[${energy}]§6飞行能量,当前可兑换§a[${count * energy}]§6飞行能量")
            else
                mutableListOf("§c无效的货币[${args[0]}]类型!")
        }
        return null
    }


}