package cn.bakamc.folia.command.base

import org.bukkit.command.CommandSender

interface BakaCommandNode {

    val command: String

    /**
     * 子命令类型只能为一种
     * 如果是[BakaCommandSubNode.Type.ARGUMENT]类型，则[subNodes]只能包含一个子指令
     */
    val subNodes: MutableList<BakaCommandSubNode>

    var permission: (BakaCommandContext) -> Boolean

    var executor: ((BakaCommandContext) -> Unit)?

    var suggestions: ((BakaCommandContext) -> List<String>?)?

    fun onCommand(context: BakaCommandContext, args: Array<out String>)

    fun onTabComplete(context: BakaCommandContext, args: Array<out String>): List<String>?

//    fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
//        //检查发送者权限
//        if (!permission(sender)) {
//            sender.sendMessage("§c你没有权限执行该指令")
//            return true
//        }
//        //当前实际执行的指令
//        val commandLine: String = buildString {
//            append("/${command.name}")
//            args.forEach { append(" $it") }
//        }
//        //如果args为空，说明是根指令直接执行
//        if (args.isEmpty()) {
//            if (depth == 0) executor?.let { it(sender) } ?: sender.sendMessage("§c无法执行该指令$commandLine")
//            return true
//        }
//        //如果args数量等于当前指令深度,说明指令结束立即执行
//        if (args.size == depth) {
//            executor?.let { it(sender) } ?: sender.sendMessage("§c无法执行该指令$commandLine")
//            return true
//        }
//
//        //查找下一个节点
//        for (sub in tree) {
//            if (!sub.isArgument) {
//                //不是参数类型，直接判断指令是否相同
//                if (sub.command == args[depth]) {
//                    sub.onCommand(sender, command, label, args)
//                    break
//                }
//            } else {
//                //是参数类型，注入参数
//                sub.args[sub.command] = args[depth]
//                sub.onCommand(sender, command, label, args)
//                break
//            }
//        }
//        return true
//    }

//    fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String>? {
//        //检查发送者权限
//        if (!permission(sender)) {
//            return mutableListOf("§c你没有权限执行该指令")
//        }
//        args.forEach {
//            println(it)
//        }
//        //非根指令才有补全
//        if (args.isNotEmpty()) {
//            if (args.size == depth) {
//                return parent?.nextSuggestions?.invoke(sender)
//            }
//            if (args.size + depth == 1) {
//                return nextSuggestions?.invoke(sender)
//            }
//        }
//        return null
//    }

}

