package moe.forpleuvoir.nebula.serialization.hjson

import moe.forpleuvoir.nebula.serialization.*
import moe.forpleuvoir.nebula.serialization.ast.*
import moe.forpleuvoir.nebula.serialization.ast.Token.*
import moe.forpleuvoir.nebula.serialization.base.*

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

object HJsonDecoder extends SyntaxDecoder {

  override def decode(tokens: List[Token]): Try[SerializeElement] = {
    tokens match {
      case Nil | EOF(_) :: _ => Success(SerializeNull)

      case Symbol("{", _) :: tail => parseObject(tail).map(_._1)
      case Symbol("[", _) :: tail => parseArray(tail).map(_._1)

      case _ =>
        if (isRootObject(tokens)) {
          parseObject(tokens).map(_._1)
        } else {
          parseElement(tokens).map(_._1)
        }
    }
  }

  private def isRootObject(tokens: List[Token]): Boolean = {
    tokens.takeWhile {
      case Symbol("}", _) | Symbol("]", _) | EOF(_) => false
      case _ => true
    }.exists {
      case Symbol(":", _) => true
      case _ => false
    }
  }

  private def parseElement(tokens: List[Token]): Try[(SerializeElement, List[Token])] = {
    tokens match {
      case Symbol("{", _) :: tail => parseObject(tail)
      case Symbol("[", _) :: tail => parseArray(tail)

      case Literal(value, _) :: tail =>
        val element = if (value == null) SerializeNull
        else SerializePrimitive(value.asInstanceOf[Primitive])
        Success(element, tail)

      case Identifier(value, _) :: tail =>
        Success(SerializePrimitive(value), tail)

      case EOF(pos) :: _ =>
        Failure(SyntaxReadException("Unexpected end of input: element expected", pos))

      case other :: _ =>
        Failure(SyntaxReadException(s"Unexpected token '$other' at element position", other.pos))

      case Nil =>
        Failure(new IllegalArgumentException("Token stream is empty"))
    }
  }

  private def parseObject(tokens: List[Token]): Try[(SerializeObject, List[Token])] = {
    val obj = SerializeObject()

    @tailrec
    def parseMembers(currentTokens: List[Token]): Try[(SerializeObject, List[Token])] = {
      currentTokens match {
        case Nil | EOF(_) :: _ => Success(obj, currentTokens)
        case Symbol("}", _) :: tail => Success(obj, tail)
        case Symbol(",", _) :: tail => parseMembers(tail)

        // 识别 Key：提取 keyToken 以便后续获取它的值和位置
        case (keyToken@(Identifier(_, _) | Literal(_, _))) :: Symbol(":", _) :: tail =>
          val keyStr = keyToken match {
            case Identifier(s, _) => s
            case Literal(v, _) => String.valueOf(v)
          }
          

          parseElement(tail) match {
            case Success((value, nextTail)) =>
              obj.put(keyStr, value)
              nextTail match {
                case Symbol(",", _) :: afterComma => parseMembers(afterComma)
                case _ => parseMembers(nextTail)
              }
            case Failure(e) => Failure(e)
          }

        case other :: _ =>
          Failure(SyntaxReadException(s"Expected key or '}', but found: $other", other.pos))
      }
    }

    parseMembers(tokens)
  }

  private def parseArray(tokens: List[Token]): Try[(SerializeArray, List[Token])] = {
    val arr = SerializeArray()

    @tailrec
    def parseElements(currentTokens: List[Token]): Try[(SerializeArray, List[Token])] = {
      currentTokens match {
        case Nil | EOF(_) :: _ => Success(arr, currentTokens)
        case Symbol("]", _) :: tail => Success(arr, tail)
        case Symbol(",", _) :: tail => parseElements(tail)

        case _ =>
          parseElement(currentTokens) match {
            case Success((value, nextTail)) =>
              arr.addOne(value)
              nextTail match {
                case Symbol(",", _) :: afterComma => parseElements(afterComma)
                case _ => parseElements(nextTail)
              }
            case Failure(e) => Failure(e)
          }
      }
    }

    parseElements(tokens)
  }
}

