package cn.bakamc.folia.command

import cn.bakamc.folia.config.Configs.FlightEnergy.MAX_ENERGY
import cn.bakamc.folia.config.Configs.FlightEnergy.MONEY_ITEM
import cn.bakamc.folia.db.table.isMatch
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import cn.bakamc.folia.flight_energy.FlightEnergyManager.energy
import cn.bakamc.folia.item.SpecialItemManager
import cn.bakamc.folia.util.toServerPlayer
import cn.bakamc.folia.util.wrapInSquareBrackets
import net.minecraft.ChatFormatting
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


//object FlyCommand : BakaCommand {
//
//    override val command: String = "fly"
//
//    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
//        if (sender !is Player) {
//            sender.sendMessage("§c只有玩家可以使用此命令！")
//            return true
//        }
//        val player = sender.toServerPlayer()!!
//        if (args.isNullOrEmpty()) {
//            FlightEnergyManager.apply {
//                if (sender.energy > 0) {
//                    toggleFly(sender)
//                    player.feedback(success("飞行状态已切换 ") + wrapInSquareBrackets(if (sender.allowFlight) success("开启") else error("关闭")))
//                } else {
//                    player.feedback(error("你的飞行能量不足,输入指令 ") + success("/fly <货币类型> <使用数量>") + error(" 购买"))
//                }
//            }
//        } else if (args.size == 1) {
//            if (args[0] == "energy") {
//                player.feedback(tip("你当前的飞行能量为") + success("[${sender.energy}]"))
//                return true
//            }
//            FlightEnergyManager.moneyItem(args[0])?.let { (item, energy) ->
//                player.feedback(tip("每个") + item(item.key) + tip("可以兑换") + success("[$energy]") + tip("飞行能量"))
//            } ?: run {
//                player.feedback(error("无效的货币类型") + item(args[0]))
//            }
//        } else if (args.size == 2) {
//            val key = args[0]
//            var count = args[1].toInt()
//
//            val moneyItem = FlightEnergyManager.moneyItem(key)
//
//            if (moneyItem != null) {
//                val (item, energy) = moneyItem
//
//                val inventory = player.inventory
//
//                //纪录扣费动作
//                val actions = mutableListOf<() -> Unit>()
//
//                inventory.items.filter { stack ->//过滤出对应的货币
//                    if (!stack.isEmpty) {
//                        item.isMatch(stack)
//                    } else false
//                }.forEach { stack ->
//                    val temp = count.coerceAtMost(stack.count)
//                    count -= temp
//
//                    actions.add {
//                        stack.count -= temp
//                    }
//
//                }
//
//                if (count == 0) {//有足够的货币
//                    val totalEnergy = energy * args[1].toInt()
//                    FlightEnergyManager.apply {
//                        return if (totalEnergy + sender.energy > MAX_ENERGY) {//超出了能量上限
//                            player.feedback(error("超出了能量上限") + format("[最大值:$MAX_ENERGY,充值后会超出:${totalEnergy + sender.energy - MAX_ENERGY}]", ChatFormatting.GREEN))
//                            false
//                        } else {//购买成功
//                            var returnValue = false
//                            //执行扣费操作
//                            runCatching {
//                                actions.forEach { it.invoke() }
//                            }.onFailure {
//                                player.feedback(error("购买失败") + success("[原因:${it.message}]"))
//                                player.feedback(error("请联截图系管理员处理"))
//                                returnValue = false
//                            }.onSuccess {
//                                sender.energy += totalEnergy
//                                player.feedback(success("成功购买") + item(item.key) + success("的能量[$totalEnergy],当前能量[${sender.energy}]"))
//                                returnValue = true
//                            }
//                            returnValue
//                        }
//                    }
//                } else {//没有足够的货币
//                    player.feedback(error("你所拥有的对应货币") + item(key) + error("数量不足"))
//                    player.feedback(error("(需要") + tip("[${args[1].toInt()}]") + error("个,在背包中找到") + tip("[${args[1].toInt() - count}]") + error("个)"))
//                }
//            }
//            player.feedback(error("无效的货币类型") + item(key))
//        }
//        return true
//    }
//
//    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String>? {
//        if (sender !is Player) {
//            return mutableListOf("§c只有玩家可以使用此命令！")
//        }
//        if (args?.size == 1) {
//            return SpecialItemManager.specifyType(MONEY_ITEM.keys).keys.toMutableList().apply {
//                add("energy")
//            }
//        } else if (args?.size == 2) {
//            val energy = MONEY_ITEM[args[0]]
//            val count = args[1].toIntOrNull() ?: 0
//            return if (energy != null)
//                mutableListOf("§6每个${args[0]}可以兑换§a[${energy}]§6飞行能量,当前可兑换§a[${count * energy}]§6飞行能量")
//            else
//                mutableListOf("§c无效的货币[${args[0]}]类型!")
//        }
//        return null
//    }
//
//
//}