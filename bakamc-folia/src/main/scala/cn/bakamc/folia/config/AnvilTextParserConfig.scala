package cn.bakamc.folia.config

import cn.bakamc.common.inlinestyletext.modifier.{ColorModifier, DecorationModifier, LegacyChatFormattingModifier}
import cn.bakamc.common.inlinestyletext.{InlineStyleTextParser, ModifierContainer}
import moe.forpleuvoir.nebula.config.item.ConfigBoolean
import moe.forpleuvoir.nebula.config.{Comment, Config}


object AnvilTextParserConfig extends PluginConfigManager("anvil_text_parser") {

  @Comment("是否启用铁砧重命名文本解析")
  val enable = ConfigBoolean("enable", true)

  @Comment("文本解析器")
  val parser: Config[InlineStyleTextParser] = Config[InlineStyleTextParser](
    "parser",
    InlineStyleTextParser(
      ModifierContainer(
        LegacyChatFormattingModifier.DEFAULT,
        DecorationModifier.DEFAULT,
        ColorModifier(false, true),
      )
    )
  )

}
