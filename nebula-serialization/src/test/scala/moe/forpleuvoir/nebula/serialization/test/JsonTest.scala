package moe.forpleuvoir.nebula.serialization.test

import moe.forpleuvoir.nebula.serialization.hjson.HJsonDialect
import moe.forpleuvoir.nebula.serialization.json.{JsonDecoder, JsonDialect, JsonLexer}
import org.junit.jupiter.api.Test

class JsonTest {

  val json: String =
    """{
      |  "name": "John Doe",
      |  "age": 30,
      |  "number": 19e+16,
      |  "en?": null,
      |  "address": {
      |    "street": "123 Main St",
      |    "city": "Cityville"
      |  },
      |  "contacts": [
      |    {
      |      "type": "email",
      |      "value": "john.do\"e@example.com"
      |    },
      |    {
      |      "type": "phone",
      |      "value": "+1234567890"
      |    }
      |  ],
      |  "notes": " {\"nestedKey\":\"nested\\\"Value\"}",
      |  "nestedJson": {
      |    "key1": "value1?§aa",
      |    "key2": "value2"
      |  },
      |  "url": "https://maven.forpleuvoir.moe"
      |}""".stripMargin

  @Test
  def test1(): Unit = {

    val tokens = JsonLexer.tokenize(json)
    val triedElement = JsonDecoder.decode(tokens)
    println(triedElement.get)
    println(JsonDialect.encode(triedElement.get))

  }


  @Test
  def test2(): Unit = {
    val json2 = """["aaa","bb",61,true,null]"""
    println(JsonDialect.parse(json2).get)

    val b = "101101"
    println(Integer.parseInt(b, 2))

  }

  val hjson: String =
    """
      |# Nebula Serialization Test File
      |// 这是一个 Root Object，不需要最外层的大括号
      |project: moe.forpleuvoir.nebula
      |version: 1.0.0
      |
      |/* 测试多行字符串和转义
      |*/
      |description:
      |  '''
      |  这是 Nebula 框架的 HJSON 序列化组件。
      |  支持多行文本，不需要 \n 转义。
      |  保留缩进和换行。
      |  '''
      |
      |# 测试数组（省略逗号，混合类型）
      |features: [
      |  "Coordinates Tracking"
      |  "Unquoted Strings"
      |  Trailing Commas Support,
      |  12345
      |  true
      |  null
      |]
      |
      |# 测试嵌套对象和特殊字符键
      |"advanced settings": {
      |  # 这里的冒号后面有空格，所以是 Key-Value
      |  enable_feature: true
      |
      |  # 这里的冒号后面没空格，在某些严格模式下可能是字符串，但你的 Lexer 处理了它
      |  debug_level: 4
      |
      |  # 包含特殊字符的键必须加引号
      |  "server:port": 8080
      |}
      |
      |# 故意留一个尾随逗号测试 Decoder 的健壮性
      |last_item: "finished",""".stripMargin

  val hjson2: String =
    """
      |# 混合极端用例
      |"key with # hash": "value with : colon"
      |"123": "this is a string, not a number"
      |boolean_string: "false"
      |empty_list: []
      |complex_array: [
      |  "   leading spaces   "
      |  "trailing spaces   "
      |  ""
      |  " "
      |]""".stripMargin

  val hjson3: String =
    """
      |# 嵌套层级测试
      |system: {
      |  # 第一层缩进
      |  metadata: {
      |    # 第二层缩进
      |    description: '''
      |      这是嵌套在第二层级的多行字符串。
      |      它的起始位置应该被正确识别。
      |        这一行比上一行多两个空格，
      |      解析后这两个相对空格应该保留。
      |      '''
      |
      |    # 测试空对象和空数组在嵌套中的表现
      |    tags: []
      |    data: {}
      |  }
      |
      |  # 测试多行字符串作为数组元素
      |  logs: [
      |    '''
      |    第一条日志内容
      |    带有换行
      |    '''
      |    '''
      |    第二条日志内容
      |    没有多余空行
      |    '''
      |  ]
      |}
      |
      |# 验证 Key 是否会因为包含空格被正确解析
      |"user settings": {
      |  profile: {
      |    bio: '''
      |      开发者
      |      来自 Nebula 项目组
      |      '''
      |  }
      |}""".stripMargin

  val hjson4: String =
    """
      |# Nebula Framework - Extreme Stress Test
      |# 目标：测试深度缩进下的多行字符串幂等性
      |
      |"global settings": {
      |  "network:v6": {
      |    enabled: true
      |    # 测试包含特殊符号和空格的 Key
      |    "proxy strategy": "round-robin"
      |
      |    # 深度嵌套的多行文本，测试去缩进（Dedent）
      |    motd: {
      |      content: '''
      |        欢迎来到 Nebula 测试服务器！
      |
      |        本行上方是一个纯空行（不带空格）。
      |          本行比第一行多两个空格（相对缩进）。
      |        本行末尾带有一些空格
      |        '''
      |    }
      |  }
      |}
      |
      |# 测试数组中的混合多行文本
      |mod_list: [
      |  '''
      |  Hiiro Sakura Core
      |  Version: 2.3.0
      |  '''
      |  # 紧跟一个带特殊字符的字符串
      |  "Lib:Mixin-Extra"
      |  # 再来一个带缩进的多行文本
      |  '''
      |      这是一个故意深缩进的块
      |      用于测试起始空格剥离
      |  '''
      |]
      |
      |# 测试转义字符的字面量表现
      |"escape_test": {
      |  # 在 HJSON 中，普通字符串不需要转义 \n，但双引号字符串需要
      |  raw_path: "C:\\Users\\Admin\\AppData"
      |  regex: "^[a-zA-Z0-9_.-]+$"
      |  multiline_with_quotes: '''
      |    "这是一段带引号的对话"
      |    '这也是一段带单引号的对话'
      |    \n 这里显示的是反斜杠和字母 n，而不是换行符
      |    '''
      |}
      |
      |# 结尾空项测试
      |empty_end: {}""".stripMargin

  @Test
  def test3(): Unit = {
    val triedElement = HJsonDialect.parse(hjson4).get
    println(triedElement)
    println("*************************************")
    val str = HJsonDialect.encode(triedElement)
    println(str)
    println("*************************************")
    println(HJsonDialect.parse(str).get)
    println("*************************************")
    println(JsonDialect.encode(triedElement))
  }


}
