package cn.bakamc.folia.util.text

import com.google.gson.{Gson, GsonBuilder, JsonParser}
import com.mojang.serialization.JsonOps
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.minecraft.network.chat.{ComponentSerialization, Component as MCComponent}
import org.bukkit.craftbukkit.CraftRegistry

given Conversion[String, Component] = Component.text(_)

lazy val GSON: Gson = (new GsonBuilder).disableHtmlEscaping.create

given Conversion[MCComponent, Component] = nms2Kyori(_)

given Conversion[Component, MCComponent] = kyori2Nms(_)

def nms2Kyori(mcComponent: MCComponent): Component = {
  val registry = CraftRegistry.getMinecraftRegistry
  val json = GSON.toJson(
    ComponentSerialization.CODEC.encodeStart(
      registry.createSerializationContext(JsonOps.COMPRESSED),
      mcComponent
    ).getOrThrow()
  )
  JSONComponentSerializer.json.deserialize(json)
}

def kyori2Nms(component: Component): MCComponent = {
  val json = JSONComponentSerializer.json.serialize(component)
  val registry = CraftRegistry.getMinecraftRegistry

  ComponentSerialization.CODEC.decode(
    registry.createSerializationContext(JsonOps.COMPRESSED),
    JsonParser.parseString(json)
  ).getOrThrow().getFirst
}

extension (component: Component) {
  def wrapInSquareBrackets: Component = {
    Component.text("[")
      .append(component)
      .append("]")
  }
}
