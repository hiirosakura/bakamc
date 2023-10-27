package cn.bakamc.folia.command.base

interface BakaCommandArgumentSubNode: BakaCommandSubNode {

    override val type: BakaCommandSubNode.Type get() = BakaCommandSubNode.Type.ARGUMENT


}