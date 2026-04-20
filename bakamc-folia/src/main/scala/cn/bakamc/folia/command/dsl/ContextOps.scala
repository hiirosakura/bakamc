package cn.bakamc.folia.command.dsl

import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

import scala.jdk.CollectionConverters.*
import scala.reflect.ClassTag
import scala.util.Try

object ContextOps {

  inline def context(using ctx: CommandContext[CommandSourceStack]): CommandContext[CommandSourceStack] = ctx

  inline def getArg[T](name: String)(using ctx: CommandContext[CommandSourceStack])(using tag: ClassTag[T]): T =
    ctx.getArgument(name, tag.runtimeClass.asInstanceOf[Class[T]])

  inline def tryGetArg[T](name: String)(using ctx: CommandContext[CommandSourceStack])(using tag: ClassTag[T]): Try[T] =
    Try(getArg(name))

  inline def getArgOption[T](name: String)(using ctx: CommandContext[CommandSourceStack])(using tag: ClassTag[T]): Option[T] =
    tryGetArg(name).toOption

  inline def source(using ctx: CommandContext[CommandSourceStack]): CommandSourceStack = {
    ctx.getSource
  }

  inline def sender(using ctx: CommandContext[CommandSourceStack]): CommandSender =
    source.getSender

  inline def senderPlayer(using ctx: CommandContext[CommandSourceStack]): Player =
    sender.asInstanceOf[Player]

  inline def feedback(message: Component)(using ctx: CommandContext[CommandSourceStack]): Unit =
    sender.sendMessage(message)

  inline def feedback(message: String)(using ctx: CommandContext[CommandSourceStack]): Unit =
    sender.sendMessage(message)


  inline def getPlayer(argName: String)(using ctx: CommandContext[CommandSourceStack]): Player = {
    getArg[PlayerSelectorArgumentResolver](argName).resolve(source).getFirst
  }

  inline def getPlayers(argName: String)(using ctx: CommandContext[CommandSourceStack]): List[Player] = {
    getArg[PlayerSelectorArgumentResolver](argName).resolve(source).asScala.toList
  }

}