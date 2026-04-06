package moe.forpleuvoir.nebula.serialization.test

import moe.forpleuvoir.nebula.serialization.json.{JsonDecoder, JsonDialect, JsonLexer}
import org.junit.jupiter.api.Test

class JsonTest {

  val json: String =
    """{
      |  "name": "John Doe",
      |  "age": 30,
      |  "number": 19e+16,
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


}
