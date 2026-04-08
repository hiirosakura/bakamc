package moe.forpleuvoir.nebula.serialization.test

import moe.forpleuvoir.nebula.serialization.extension.SerObjectOps.*
import moe.forpleuvoir.nebula.serialization.extension.buildSerObject
import moe.forpleuvoir.nebula.serialization.hjson.CommentedHJsonEncoder

// 1. 实现一个具体的测试类
class TestConfigEncoder(commentMap: Map[String, String]) extends CommentedHJsonEncoder {
  // 实现接口，让 Encoder 能拿到注释
  override def getComment(path: String): Option[String] = commentMap.get(path)
}

object HJsonCommentTest {
  def main(args: Array[String]): Unit = {
    // 2. 模拟注释 Map（由路径寻址）
    val myComments = Map(
      "project" -> "Nebula 框架核心配置",
      "project.version" -> "当前项目的版本号",
      "project.developer" -> "开发者信息\n来自 Nebula 项目组\n负责 Hiiro Sakura 模块", // 测试多行
      "settings" -> "全局设置项",
      "settings.network" -> "网络相关设置\n包含 IPv4 和 IPv6", // 测试多行
      "settings.network.port" -> "服务器监听端口"
    )

    // 3. 构建一个复杂的 SerializeObject
    val root = buildSerObject {
      "project" := buildSerObject {
        "name" := "Nebula-Core"
        "version" := "1.2.0"
        "developer" := "moe.forpleuvoir"
      }
      "settings sd" := buildSerObject {
        "network" := buildSerObject {
          "port" := 8080
          "protocol" := "TCP"
        }
        "debug" := true
      }
    }

    // 4. 执行编码
    val encoder = new TestConfigEncoder(myComments)
    val result = encoder.encode(root)

    // 5. 打印结果
    println("=" * 20 + " Generated HJSON " + "=" * 20)
    println(result)
    println("=" * 57)
  }
}