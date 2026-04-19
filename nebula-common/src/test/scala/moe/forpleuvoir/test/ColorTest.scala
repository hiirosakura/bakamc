package moe.forpleuvoir.test

import moe.forpleuvoir.nebula.common.color.{Color, Colors}
import moe.forpleuvoir.nebula.common.util.primitive.BooleanExtension.*
import org.junit.jupiter.api.Test

import java.io.File
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.Files
import scala.collection.mutable
import scala.jdk.CollectionConverters.CollectionHasAsScala

class ColorTest {

  @Test
  def test1(): Unit = {
    val c = Color.fromARGB(1.0, 173, 255, 0.6)
    println(c.toHexStr)
    c.alpha(0.5f)
    c.red(.75f)
    c.green(222)
    c.blue(123)

    println(Colors.NIGHT.display)
  }


  @Test
  def colorMap(): Unit = {
    val colors = File("build\\Colors.kt").readLines()

    val outFile = File("build\\Colors.scala")

    val list = mutable.ListBuffer.empty[String]

    val result = colors.filter(!_.isBlank).map { line =>
      if (line.contains("@JvmStatic")) ""
      else line.replace("val", "lazy val")
        .replace("get()", "")
        .replace("Color(", "Color.fromARGB(")
    }.mkString("\n")

    Files.writeString(outFile.toPath, result, StandardCharsets.UTF_8)

  }

  extension (file: File) {
    def readLines(charset: Charset = StandardCharsets.UTF_8): List[String] = {
      Files.readAllLines(file.toPath, charset).asScala.toList
    }

  }

}

