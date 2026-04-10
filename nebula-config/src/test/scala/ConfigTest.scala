import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.config.component.LocalConfig
import moe.forpleuvoir.nebula.config.item.{ConfigBoolean, ConfigColor, ConfigInt, ConfigString}
import moe.forpleuvoir.nebula.config.persistence.HJsonConfigPersistence
import moe.forpleuvoir.nebula.config.{Comment, ConfigGroup, ConfigManager}
import moe.forpleuvoir.nebula.serialization.hjson.HJsonDialect
import org.junit.jupiter.api.Test

import java.nio.file.Path

class ConfigTest {


  @Test
  def test1(): Unit = {
    TestConfig.startup()
    println("**********************")
    println(HJsonDialect.encode(TestConfig.serialization))
  }

}


object TestConfig extends ConfigManager("test_config") {

  LocalConfig(Path.of("./build/config"), HJsonConfigPersistence(this))

  val test1: ConfigString = ConfigString("asd", "str")
    .comment("这是一行注释")

  @Comment(text = "这是Group的注解2")
  object Group extends ConfigGroup("group") {

    @Comment(text = "这是Group.test1的注解2\n多行注释测试一下")
    val test1 = ConfigString("test", "test")

    @Comment(text = "这是颜色")
    val color = ConfigColor("color",Color.fromARGB(0xFF66CCFF))
  }

  val woqu: ConfigString = ConfigString("woqu", "str")
    .comment("这是一行注释")

  object Group2 extends ConfigGroup("group2") {
    val test2 = ConfigInt("num", 42)
  }

  object Group3 extends ConfigGroup("group3") {
    val test3 = ConfigBoolean("flag", true)
  }

}