import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.config.component.LocalConfig
import moe.forpleuvoir.nebula.config.item.*
import moe.forpleuvoir.nebula.config.persistence.HJsonConfigPersistence
import moe.forpleuvoir.nebula.config.{Comment, ConfigGroup, ConfigManager}
import moe.forpleuvoir.nebula.serialization.codec.{Codec, given_Codec_Color}
import moe.forpleuvoir.nebula.serialization.hjson.HJsonDialect
import org.junit.jupiter.api.Test

import java.nio.file.Path
import scala.concurrent.duration.*

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

  @Comment(text = "这是多行文本的注释")
  val str2: ConfigString = ConfigString("str2",
    """|这是多行文本
       |这是第二行
       |这是第三行
       |""".stripMargin
  )

  val c: ConfigJavaEnum[NebulaColor] = ConfigJavaEnum("enum", NebulaColor.GREEN)

  val c2: ConfigEnum[NebulaEnum] = ConfigEnum("enum2", NebulaEnum.RED)

  val dur: ConfigDuration = ConfigDuration("dur", 1000.second)

  val dur2: ConfigFiniteDuration = ConfigFiniteDuration("dur2", 1000.day)

  given Codec[Int] = Codec.Int

  given Codec[Float] = Codec.Float

  val tupleList: ConfigList[(Int, Color)] = ConfigList[(Int, Color)]("tuple_list", List((1, Color.fromARGB(0xFF66CCFF)), (2, Color.fromARGB(0xFF66CCFF)), (3, Color.fromARGB(0xFF66CCFF))))(using Codec.derived)

  val intList: ConfigList[Int] = ConfigList[Int]("int_list", List(1, 2, 3))

  val floatList: ConfigList[Float] = ConfigList.of("float_list", 1.0f, 2.0f, 3.0f)

  val intMap = ConfigMap[Int]("int_map", Map("1" -> 1, "2" -> 2, "3" -> 3))

  val floatMap: ConfigMap[Float] = ConfigMap.of[Float]("float_map", "1" -> 1.0f, "2" -> 2.0f, "3" -> 3.0f)

  @Comment(text = "这是Group的注解2")
  object Group extends ConfigGroup("group") {

    @Comment(text = "这是Group.test1的注解2\n多行注释测试一下")
    val test1 = ConfigString("test", "test")

    @Comment(text = "这是颜色")
    val color = ConfigColor("color", Color.fromARGB(0xFF66CCFF))
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

enum NebulaEnum derives Codec {
  case RED, GREEN, BLUE
}