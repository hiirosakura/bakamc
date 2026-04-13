package cn.bakamc.common

import moe.forpleuvoir.nebula.serialization.codec.{Codec, given_Codec_String}

case class ServerMeta(name: String) derives Codec

object ServerMeta {
  def apply(name: String): ServerMeta = new ServerMeta(name)
}