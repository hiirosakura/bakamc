package cn.bakamc.folia.command.base

class ArgumentCommandSubNode(command: String, parent: CommandNode) : CommandSubNode(command, parent) {

    override val type: Type get() = Type.ARGUMENT


}