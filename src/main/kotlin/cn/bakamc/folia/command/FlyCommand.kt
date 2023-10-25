package cn.bakamc.folia.command

import cn.bakamc.folia.command.SpecialItemCommand.feedback
import cn.bakamc.folia.config.Configs.FlightEnergy.MAX_ENERGY
import cn.bakamc.folia.config.Configs.FlightEnergy.MONEY_ITEM
import cn.bakamc.folia.db.table.isMatch
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import kotlinx.coroutines.runBlocking
import net.minecraft.server.level.ServerPlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object FlyCommand : BakaCommand {

    override val subCommand: String = "fly"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§c只有玩家可以使用此命令！")
            return false
        }
        sender as ServerPlayer
        if (args.isNullOrEmpty()) {
            FlightEnergyManager.apply {
                return if (sender.energy > 0) {
                    toggleFly(sender)
                    sender.feedback(success("飞行状态已切换 ") + "§a[${if (sender.allowFlight) "§a开启" else "§c关闭"}§a]")
                    true
                } else {
                    sender.feedback(error("你的飞行能量不足,输入指令 ") + success("/baka-fly <货币类型> <使用数量>") + error(" 购买"))
                    false
                }
            }
        } else if (args.size == 1) {
            return FlightEnergyManager.moneyItem(args[0])?.let { (item, energy) ->
                sender.feedback(tip("每个") + item(item.key) + tip("可以兑换") + "§a[$energy]" + tip("飞行能量"))
                true
            } ?: run {
                sender.feedback(error("无效的货币类型") + item(args[0]))
                false
            }
        } else if (args.size == 2) {
            val key = args[0]
            var count = args[1].toInt()

            val moneyItem = FlightEnergyManager.moneyItem(key)

            if (moneyItem != null) {
                val (item, energy) = moneyItem

                val inventory = (sender as ServerPlayer).inventory

                //纪录扣费动作
                val actions = mutableListOf<() -> Unit>()

                inventory.items.filter { stack ->//过滤出对应的货币
                    if (!stack.isEmpty) {
                        item.isMatch(stack)
                    } else false
                }.forEach { stack ->
                    val temp = count.coerceAtMost(stack.count)
                    count -= temp

                    actions.add {
                        stack.count -= temp
                    }

                }

                if (count == 0) {//有足够的货币
                    val totalEnergy = energy * args[1].toInt()
                    FlightEnergyManager.apply {
                        return if (totalEnergy + sender.energy > MAX_ENERGY) {//超出了能量上限
                            sender.feedback(error("超出了能量上限") + "§a[最大值:$MAX_ENERGY,充值后会超出:${totalEnergy + sender.energy - MAX_ENERGY}]")
                            false
                        } else {//购买成功
                            var returnValue = false
                            //执行扣费操作
                            runCatching {
                                actions.forEach { it.invoke() }
                            }.onFailure {
                                sender.feedback(error("购买失败") + "§a[原因:${it.message}]")
                                sender.feedback(error("请联截图系管理员处理"))
                                returnValue = false
                            }.onSuccess {
                                sender.energy += totalEnergy
                                sender.feedback(success("成功购买") + item(item.key) + success("的能量") + "§a[$totalEnergy]" + success(",当前能量") + "§a[${sender.energy}]")
                                returnValue = true
                            }
                            returnValue
                        }
                    }
                } else {//没有足够的货币
                    sender.feedback(error("你所拥有的对应货币") + item(key) + error("数量不足"))
                    sender.feedback(error("(需要") + "§6[${args[1].toInt()}]" + error("个,在背包中找到" + "§6[${args[1].toInt() - count}]" + error("个")))
                    return false
                }
            }
            sender.feedback(error("无效的货币类型") + item(key))
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