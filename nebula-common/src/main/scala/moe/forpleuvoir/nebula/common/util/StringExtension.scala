package moe.forpleuvoir.nebula.common.util

extension (self: String) {

  /**
   * 如果字符串长度小于[length],则填充字符[fillChar]至长度[length]
   *
   * @param length   需要填充的长度
   * @param fillChar 填充用地字符
   * @param before   true:在原字符串前填充,false:在原字符串后填充
   * @return 填充后的字符串
   */
  def fill(length: Int, fillChar: Char, before: Boolean): String = {
    val i = length - self.length
    val sb = new StringBuilder
    if (i > 0) {
      for (_ <- 0 until i) {
        sb.append(fillChar)
      }
    }
    if (before) sb.toString() + self else self + sb.toString()
  }

  /**
   * 如果字符串长度小于[length],则填充字符[fillChar]至长度[length]
   *
   * @param length   需要填充的长度
   * @param fillChar 填充用地字符
   * @return 填充后的字符串
   */
  def fillBefore(length: Int, fillChar: Char): String = fill(length, fillChar, before = true)

  /**
   * 如果字符串长度小于[length],则填充字符[fillChar]至长度[length]
   *
   * @param length   需要填充的长度
   * @param fillChar 填充用地字符
   * @return 填充后的字符串
   */
  def fillAfter(length: Int, fillChar: Char): String = fill(length, fillChar, before = false)

  def replace(map: Map[CharSequence, CharSequence]): String = {
    var temp = self
    map.foreach(kv => {
      temp = temp.replace(kv._1, kv._2)
    })
    temp
  }

  def replace(origin: Array[CharSequence], newValue: CharSequence): String = {
    var temp: String = self
    origin.foreach(o => {
      temp = temp.replace(o, newValue)
    })
    temp
  }

}
