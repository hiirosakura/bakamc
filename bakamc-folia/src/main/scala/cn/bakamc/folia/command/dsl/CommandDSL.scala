package cn.bakamc.folia.command.dsl

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.{ArgumentBuilder, LiteralArgumentBuilder, RequiredArgumentBuilder}
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.minecraft.commands.SharedSuggestionProvider

import java.util.function.Predicate
import scala.jdk.CollectionConverters.IterableHasAsJava

trait ArgumentScope[T <: ArgumentBuilder[CommandSourceStack, T]] {
  def argumentBuilder: ArgumentBuilder[CommandSourceStack, T]
}

case class LiteralArgumentScope private[command](name: String) extends ArgumentScope[LiteralArgumentBuilder[CommandSourceStack]] {
  private val builder = LiteralArgumentBuilder.literal[CommandSourceStack](name)

  override def argumentBuilder: LiteralArgumentBuilder[CommandSourceStack] = builder
}

case class RequiredArgumentScope[A] private[command](
  name: String,
  argumentType: ArgumentType[A]
) extends ArgumentScope[RequiredArgumentBuilder[CommandSourceStack, A]] {
  private val builder = RequiredArgumentBuilder.argument[CommandSourceStack, A](name, argumentType)

  override def argumentBuilder: RequiredArgumentBuilder[CommandSourceStack, A] = builder
}

object ArgumentScope {

  def literal(name: String)(scope: LiteralArgumentScope ?=> Unit)(using parentScope: ArgumentScope[?]): Unit = {
    val literal = LiteralArgumentScope(name)
    scope(using literal)
    parentScope.argumentBuilder.`then`(literal.argumentBuilder)
  }

  def argument[T](name: String, argumentType: ArgumentType[T])(scope: RequiredArgumentScope[T] ?=> Unit)(using parentScope: ArgumentScope[?]): Unit = {
    val required = RequiredArgumentScope[T](name, argumentType)
    scope(using required)
    parentScope.argumentBuilder.`then`(required.argumentBuilder)
  }

  def requires(requirement: CommandSourceStack => Boolean)(using parentScope: ArgumentScope[?]): Unit = {
    parentScope.argumentBuilder.requires(requirement.asInstanceOf[Predicate[CommandSourceStack]])
  }

  def requires(permission: String)(using parentScope: ArgumentScope[?]): Unit = {
    parentScope.argumentBuilder.requires(_.getSender.hasPermission(permission))
  }

  def execute(action: CommandContext[CommandSourceStack] ?=> Unit)(using parentScope: ArgumentScope[?]): Unit = {
    parentScope.argumentBuilder.executes { ctx => action(using ctx); 1 }
  }

  def executes(action: CommandSourceStack => Int)(using parentScope: ArgumentScope[?]): Unit = {
    parentScope.argumentBuilder.executes(action.asInstanceOf[Command[CommandSourceStack]])
  }

  def suggests[A](provider: SuggestionProvider[CommandSourceStack])(using parentScope: RequiredArgumentScope[A]): Unit = {
    parentScope.argumentBuilder.suggests(provider)
  }

  def suggests[A](candidates: String*)(using parentScope: RequiredArgumentScope[A]): Unit = {
    parentScope.argumentBuilder.suggests { (_, builder) =>
      SharedSuggestionProvider.suggest(candidates.asJava, builder)
    }
  }

  extension (name: String) {
    def apply(scope: LiteralArgumentScope ?=> Unit)(using parentScope: ArgumentScope[?]): Unit = {
      ArgumentScope.literal(name)(scope)
    }
  }

  export ContextOps._
}


