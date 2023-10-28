package cn.bakamc.folia.command.base

class LiteralCommandSubNode(command: String, parent: CommandNode) : CommandSubNode(command, parent) {
    override val type: Type get() = Type.LITERAL

}