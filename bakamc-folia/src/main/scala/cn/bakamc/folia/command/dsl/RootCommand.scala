package cn.bakamc.folia.command.dsl

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.{CommandSourceStack, Commands}

case class RootCommand(
  node: LiteralCommandNode[CommandSourceStack],
  description: Option[String] = None
)

object RootCommand {

  def apply(name: String)(scope: LiteralArgumentScope ?=> Unit): RootCommand = {
    val literal = LiteralArgumentScope(name)
    scope(using literal)
    RootCommand(literal.argumentBuilder.build())
  }


  extension (root: RootCommand) {

    def register(registrar: Commands): Unit = {
      root.description match {
        case Some(desc) => registrar.register(root.node, desc)
        case None => registrar.register(root.node)
      }
    }
  }

}