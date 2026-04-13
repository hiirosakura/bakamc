package cn.bakamc.common.inlinestyletext

object Lexer {

  def tokenize(input: String): List[Token] = {
    val tokens = collection.mutable.ListBuffer.empty[Token]
    val chars = input.toCharArray
    var i = 0

    while (i < chars.length) {
      // 检查是否匹配到 &{
      if (i + 1 < chars.length && chars(i) == '&' && chars(i + 1) == '{') {
        tokens += Token.ControlStart(i)
        i += 2

        // 进入表达式内部处理逻辑
        var start = i
        while (i < chars.length && chars(i) != '}') {
          // 检查内部的逗号分隔符
          if (chars(i) == ',') {
            if (i > start) {
              tokens += Token.Expression(input.substring(start, i), start)
            }
//            tokens += Token.Separator(i)
            start = i + 1
          }
          i += 1
        }

        // 收集最后一个表达式片段（如果存在）
        if (i > start && i <= chars.length) {
          tokens += Token.Expression(input.substring(start, i), start)
        }

        // 检查并添加 ControlEnd
        if (i < chars.length && chars(i) == '}') {
          tokens += Token.ControlEnd(i)
          i += 1
        }
      } else {
        // 处理普通文本 (Literal)
        val start = i
        // 扫描直到遇到下一个 &{ 或字符串结束
        while (i < chars.length && !(i + 1 < chars.length && chars(i) == '&' && chars(i + 1) == '{')) {
          i += 1
        }
        if (i > start) {
          tokens += Token.Literal(input.substring(start, i), start)
        }
      }
    }

    tokens.toList
  }
}