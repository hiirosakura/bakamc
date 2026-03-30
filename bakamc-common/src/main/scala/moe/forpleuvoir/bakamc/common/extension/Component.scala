package moe.forpleuvoir.bakamc.common.extension

import net.kyori.adventure.text.{Component, ComponentLike, TextComponent, TranslatableComponent}

def Literal(content: String): TextComponent = Component.text(content)

def Translate(key: String, fallback: String | Null = null)(args: ComponentLike*): TranslatableComponent = Component.translatable(key, fallback, args *)

