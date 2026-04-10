package moe.forpleuvoir.nebula.config

trait ExceptionHandler {

  def onSerializationException(config: ConfigNode, e: SerializationException): Unit

  def onDeserializationException(config: ConfigNode, e: DeserializationException): Unit

}


object ExceptionHandler {

  val Terminal: ExceptionHandler = new ExceptionHandler {
    override def onSerializationException(config: ConfigNode, e: SerializationException): Unit = {
      e.printStackTrace()
    }

    override def onDeserializationException(config: ConfigNode, e: DeserializationException): Unit = {
      e.printStackTrace()
    }
  }

  val Throw: ExceptionHandler = new ExceptionHandler {
    override def onSerializationException(config: ConfigNode, e: SerializationException): Unit = {
      throw e
    }

    override def onDeserializationException(config: ConfigNode, e: DeserializationException): Unit = {
      throw e
    }
  }

}

class SerializationException(
  message: String,
  cause: Throwable | Null = null
) extends RuntimeException(message, cause)

class DeserializationException(
  message: String,
  cause: Throwable | Null = null
) extends RuntimeException(message, cause)