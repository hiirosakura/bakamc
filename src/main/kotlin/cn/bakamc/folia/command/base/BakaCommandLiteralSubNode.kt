package cn.bakamc.folia.command.base

interface BakaCommandLiteralSubNode : BakaCommandSubNode {
    override val type: BakaCommandSubNode.Type get() = BakaCommandSubNode.Type.LITERAL

}