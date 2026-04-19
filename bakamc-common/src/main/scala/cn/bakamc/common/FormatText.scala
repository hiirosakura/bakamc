package cn.bakamc.common

import net.kyori.adventure.text.Component

extension (sc: StringContext) {

  def comp(args: Any*)(using adapter: ComponentAdapter): Component = {
    val strings = sc.parts.iterator
    val expressions = args.iterator

    var builder = Component.text(strings.next())

    while (strings.hasNext) {
      builder = builder.append(adapter.convert(expressions.next()))
      builder = builder.append(Component.text(strings.next()))
    }

    builder
  }

}

trait ComponentAdapter {
  def convert(input: Any): Component
}