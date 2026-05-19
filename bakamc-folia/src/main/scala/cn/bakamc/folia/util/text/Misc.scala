package cn.bakamc.folia.util.text

import com.google.gson.{Gson, GsonBuilder}
import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.minecraft.network.chat.Component as MCComponent

import scala.language.implicitConversions

given Conversion[String, Component] = Component.text(_)

lazy val GSON: Gson = (new GsonBuilder).disableHtmlEscaping.create

given Conversion[MCComponent, Component] = nms2Kyori(_)

given Conversion[Component, MCComponent] = kyori2Nms(_)

def nms2Kyori(mcComponent: MCComponent): Component = {
  PaperAdventure.asAdventure(mcComponent)
}

def kyori2Nms(component: Component): MCComponent =
  PaperAdventure.asVanilla(component)

extension (component: Component) {
  def wrapInSquareBrackets: Component = {
    Component.text("[")
      .append(component)
      .append("]")
  }
}


extension (self: Option[Component]) {
  def plainText: Option[String] = self.map(_.plainText)
}

extension (self: Component) {
  def plainText: String = PlainTextComponentSerializer.plainText().serialize(self)
}