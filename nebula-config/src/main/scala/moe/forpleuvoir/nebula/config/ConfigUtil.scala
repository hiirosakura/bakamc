package moe.forpleuvoir.nebula.config

import moe.forpleuvoir.nebula.common.util.use

import java.io.*
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.Path
import java.util.UUID
import scala.io.{Codec, Source}

object ConfigUtil {

  var charset: Charset = StandardCharsets.UTF_8

  def configFile(configFileName: String, path: Path, create: Boolean = true): File = {
    val p = path.toFile
    val f = File(p, configFileName)
    if (!f.exists() && create) {
      if (!p.exists()) {
        p.mkdir()
      }
      try f.createNewFile()
      catch {
        case e: Exception =>
          throw new IOException(s"${e.getMessage},${f.getAbsolutePath}")
      }
    }
    f
  }

  def writeToFile(content: Array[Byte] | String, file: File): Unit = {
    var fileTmp = File(file.getParentFile, file.getName + ".tmp")
    if (fileTmp.exists()) {
      fileTmp = File(file.getParentFile, UUID.randomUUID().toString + ".tmp")
    }

    new FileOutputStream(fileTmp).use { it =>
      content match {
        case c: String =>
          new OutputStreamWriter(it, charset).use { osw =>
            osw.write(c)
          }
        case c: Array[Byte] =>
          it.write(c)
      }
    }
    if (file.exists() && file.isFile && !file.delete()) {
      throw new IOException(s"Failed to delete file ${file.getAbsolutePath}")
    }
    if (!fileTmp.renameTo(file)) {
      throw new IOException(s"Failed to rename temp file to ${file.getAbsolutePath}")
    }
  }

  def readFileToString(file: File): String = {
    if (file.exists() && file.isFile && file.canRead) {
      val is = new FileInputStream(file)
      try Source.fromInputStream(is)(using Codec(charset)).mkString
      finally is.close()
    } else {
      throw new IOException(s"Failed to read the file ${file.getAbsolutePath}")
    }
  }

  def readFile(file: File): Array[Byte] = {
    if (file.exists() && file.isFile && file.canRead) {
      val is = new FileInputStream(file)
      try is.readAllBytes()
      finally is.close()
    } else {
      throw new IOException(s"Failed to read the file ${file.getAbsolutePath}")
    }
  }


}
