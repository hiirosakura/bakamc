package cn.bakamc.folia.command

import cn.bakamc.folia.config.Configs.FlightEnergy.MAX_ENERGY
import cn.bakamc.folia.config.Configs.FlightEnergy.MONEY_ITEM
import cn.bakamc.folia.db.table.isMatch
import cn.bakamc.folia.db.table.toItemStack
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import cn.bakamc.folia.flight_energy.FlightEnergyManager.energy
import cn.bakamc.folia.item.SpecialItemManager
import cn.bakamc.folia.util.toServerPlayer
import cn.bakamc.folia.util.wrapInSquareBrackets
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


@Suppress("FunctionName")
internal fun FlyCommand(): BakaCommand = root("fly") {
    execute(toggleFly)
    literal("recharge") {
        argument("money_item") {
            suggestion { SpecialItemManager.specifyType(MONEY_ITEM.keys).keys.toList() }
            execute(recharge)
            argument("count") {
                suggestion {
                    val itemKey = getArg("money_item")!!
                    val energy = MONEY_ITEM[itemKey]
                    val count = getArg("count")?.toInt() ?: 1
                    if (energy != null)
                        listOf("§6每个${itemKey}可以兑换§a[${energy}]§6飞行能量,当前可兑换§a[${count * energy}]§6飞行能量")
                    else
                        listOf("§c无效的货币[${itemKey}]类型!")
                }
                execute(recharge)
            }
        }
    }
    literal("energy") {
        execute {
            val player = (it as Player).toServerPlayer()!!
            player.feedback(tip("你当前的飞行能量为") + success("[${it.energy}]"))
        }
    }
}


private val toggleFly: BakaCommand.(sender: CommandSender) -> Unit = { sender ->
    if (sender !is Player) {
        sender.sendMessage("§c只有玩家可以使用此命令！")
    }
    val player = (sender as Player).toServerPlayer()!!
    FlightEnergyManager.apply {
        if (sender.energy > 0) {
            toggleFly(sender)
            player.feedback(success("飞行状态已切换 ") + wrapInSquareBrackets(if (sender.allowFlight) success("开启") else error("关闭")))
        } else {
            player.feedback(error("你的飞行能量不足,输入指令 ") + success("/fly <货币类型> <使用数量>") + error(" 购买"))
        }
    }
}

private val recharge: BakaCommand.(sender: CommandSender) -> Unit = { sender ->
    if (sender !is Player) {
        sender.sendMessage("§c只有玩家可以使用此命令！")
    }
    val player = (sender as Player).toServerPlayer()!!

    val key = getArg("money_item")!!

    val count = getArg("count")?.toInt() ?: 1

    var countTemp = count

    val moneyItem = FlightEnergyManager.moneyItem(key)

    if (moneyItem != null) {
        val (item, energy) = moneyItem

        val inventory = player.inventory

        //纪录扣费动作
        val actions = mutableListOf<() -> Unit>()

        //过滤出对应的货币
        inventory.items.filter { stack ->
            if (!stack.isEmpty) {
                item.isMatch(stack)
            } else false
        }.forEach { stack ->
            val temp = countTemp.coerceAtMost(stack.count)
            countTemp -= temp

            actions.add {
                stack.count -= temp
            }

        }

        //是否有充足的货币
        if (countTemp == 0) {
            //有足够的货币
            val totalEnergy = energy * getArg("count")?.toInt()!!
            FlightEnergyManager.apply {
                //判断是否超出能量上限
                if (totalEnergy + sender.energy > MAX_ENERGY) {
                    //超出了能量上限
                    player.feedback(error("超出了能量上限") + success("[最大值:${MAX_ENERGY},充值后会超出:${(totalEnergy + sender.energy) - MAX_ENERGY}]"))
                } else {
                    //没有超出上限
                    runCatching {
                        //执行扣费操作
                        actions.forEach { it.invoke() }
                    }.onFailure {
                        //出现异常
                        player.feedback(error("购买失败") + success("[原因:${it.message}]"))
                        player.feedback(error("请联截图系管理员处理"))
                    }.onSuccess {
                        //结算能量
                        sender.energy += totalEnergy
                        player.feedback(success("成功购买") + item(item.toItemStack(count)!!) + success("的能量[$totalEnergy],当前能量[${sender.energy}]"))
                    }
                }
            }
        } else {
            //没有足够的货币
            player.feedback(error("你所拥有的对应货币") + item(key) + error("数量不足"))
            player.feedback(error("(需要") + tip("[$count]") + error("个,在背包中找到") + tip("[${count - countTemp}]") + error("个)"))
        }
    }
}