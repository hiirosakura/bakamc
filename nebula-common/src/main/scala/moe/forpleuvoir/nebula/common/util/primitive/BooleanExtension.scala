package moe.forpleuvoir.nebula.common.util.primitive

object BooleanExtension {

  extension (self: Boolean | Null) {

    def either[T](_if: => T)(_else: => T): T =
      if (self == true) _if else _else

    def onTrue[T](_true: => T): Option[T] =
      Option.when(self == true)(_true)

    def onFalse[T](_false: => T): Option[T] =
      Option.when(!(self == true))(_false)

  }

  extension (self: Option[Boolean]) {

    def either[T](_if: => T)(_else: => T): T =
      if (self.getOrElse(false)) _if else _else

    def onTrue[T](_true: => T): Option[T] =
      Option.when(self.getOrElse(false))(_true)

    def onFalse[T](_false: => T): Option[T] =
      Option.when(!self.getOrElse(false))(_false)

  }
}
