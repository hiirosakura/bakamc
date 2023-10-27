package cn.bakamc.folia.command.base

interface BakaCommandSubNode : BakaCommandNode {
    enum class Type {
        LITERAL,
        ARGUMENT
    }

    val parent: BakaCommandNode?

    val type: Type

}