package moe.forpleuvoir.nebula.common.util

enum Result[T] {
  case Success(value: T)
  case Failure(exception: Throwable)

  def get: Option[T] = this match {
    case Success(value) => Some(value)
    case Failure(_) => None
  }

  def getOrThrow: T = this match {
    case Success(value) => value
    case Failure(exception) => throw exception
  }

  def getOrElse(default: => T): T = this match {
    case Success(value) => value
    case Failure(_) => default
  }

  def onSuccess(block: T => Unit): Result[T] = {
    this match {
      case Success(value) => block(value)
      case Failure(_) =>
    }
    this
  }

  def isSuccess: Boolean = this match {
    case Success(_) => true
    case Failure(_) => false
  }

  def onFailure(block: Throwable => Unit): Result[T] = {
    this match {
      case Success(_) =>
      case Failure(exception) => block(exception)
    }
    this
  }

  def isFailure: Boolean = this match {
    case Success(_) => false
    case Failure(_) => true
  }

}

inline def runCatching[R](inline block: => R): Result[R] = {
  try {
    Result.Success(block)
  } catch {
    case e: Throwable => Result.Failure(e)
  }
}

def runCatching[R](block: R): Result[R] = {
  try {
    Result.Success(block)
  } catch {
    case e: Throwable => Result.Failure(e)
  }
}

extension [T](self: T) {
  inline def runCatching[R](block: T => R): Result[R] = {
    try {
      Result.Success(block(self))
    } catch {
      case e: Throwable => Result.Failure(e)
    }
  }
}