package moe.forpleuvoir.nebula.serialization.hjson

import moe.forpleuvoir.nebula.serialization.base.{SerializeElement, SerializeObject}

trait HJsonCommentedEncoder extends HJsonEncoder {

  def getComment(path: String): Option[String]

  /**
   * 路径上下文，记录当前处理到的层级
   */
  protected var currentPath: List[String] = Nil

  /**
   * 重写根对象入口，确保路径初始状态正确
   */
  override protected def encodeRootObject(obj: SerializeObject): String = {
    currentPath = Nil // 清空路径历史
    super.encodeRootObject(obj)
  }

  override protected def encodeEntry(key: String, value: SerializeElement, indent: Int, spacing: String): String = {
    // 1. 获取路径
    val previousPath = currentPath
    currentPath = currentPath :+ key
    val pathStr = currentPath.mkString(".")

    // 2. 构建注释字符串
    val sb = new StringBuilder
    getComment(pathStr).foreach { comment =>
      comment.linesIterator.foreach { line =>
        // 使用传入的 spacing，确保注释和下方的 Key 对齐
        sb.append(spacing).append("# ").append(line).append("\n")
      }
    }

    // 3. 调用父类生成原始的 "spacing key: value"
    val entryLine = super.encodeEntry(key, value, indent, spacing)

    // 4. 恢复路径
    currentPath = previousPath

    // 5. 返回 [注释 + Entry行]
    sb.append(entryLine).toString()
  }

}