package moe.forpleuvoir.nebula.serialization.json

import moe.forpleuvoir.nebula.serialization.*
import moe.forpleuvoir.nebula.serialization.ast.*
import moe.forpleuvoir.nebula.serialization.ast.Token.*
import moe.forpleuvoir.nebula.serialization.base.*

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

object JsonDecoder extends SyntaxDecoder {

  override def decode(tokens: List[Token]): Try[SerializeElement] = {
    tokens match {
      case Nil | EOF(_) :: _ =>
        Failure(new IllegalArgumentException("Empty token stream"))

      case _ =>
        parseElement(tokens).flatMap { (element, remaining) =>
          remaining match {
            case Nil | EOF(_) :: _ => Success(element)
            case other :: _ =>
              Failure(SyntaxReadException(s"Unexpected token after JSON expression: $other", other.pos))
          }
        }
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

      case head :: _ =>
        Failure(SyntaxReadException(s"Unexpected token '$head' at element position", head.pos))

      case Nil =>
        Failure(new IllegalArgumentException("Unexpected end of input"))
    }
  }

  private def parseObject(tokens: List[Token]): Try[(SerializeObject, List[Token])] = {
    val obj = SerializeObject()

    @tailrec
    def parseMembers(currentTokens: List[Token]): Try[(SerializeObject, List[Token])] = {
      currentTokens match {
        // 空对象情况
        case Symbol("}", _) :: tail => Success(obj, tail)

        // 标准 JSON Key 必须是 Literal(String)
        case Literal(key: String, _) :: Symbol(":", _) :: tail =>
          parseElement(tail) match {
            case Success((value, nextTail)) =>
              obj.put(key, value)
              nextTail match {
                case Symbol(",", _) :: afterComma =>
                  // JSON 不允许尾随逗号，所以逗号后必须跟 key
                  afterComma match {
                    case Symbol("}", pos) :: _ =>
                      Failure(SyntaxReadException("Trailing comma is not allowed in JSON", pos))
                    case _ => parseMembers(afterComma)
                  }
                case Symbol("}", _) :: afterClose => Success(obj, afterClose)
                case other :: _ =>
                  Failure(SyntaxReadException(s"Expected ',' or '}', found $other", other.pos))
                case Nil =>
                  Failure(new IllegalArgumentException("Unexpected EOF in object"))
              }
            case Failure(e) => Failure(e)
          }

        case other :: _ =>
          Failure(SyntaxReadException(s"Expected string key or '}', found $other", other.pos))

        case Nil =>
          Failure(new IllegalArgumentException("Unexpected end of input in object members"))
      }
    }

    parseMembers(tokens)
  }

  private def parseArray(tokens: List[Token]): Try[(SerializeArray, List[Token])] = {
    val arr = SerializeArray()

    @tailrec
    def parseElements(currentTokens: List[Token]): Try[(SerializeArray, List[Token])] = {
      currentTokens match {
        case Symbol("]", _) :: tail => Success(arr, tail)

        case _ =>
          parseElement(currentTokens) match {
            case Success((element, nextTail)) =>
              arr.addOne(element)
              nextTail match {
                case Symbol(",", _) :: afterComma =>
                  // JSON 不允许尾随逗号
                  afterComma match {
                    case Symbol("]", pos) :: _ =>
                      Failure(SyntaxReadException("Trailing comma is not allowed in JSON array", pos))
                    case _ => parseElements(afterComma)
                  }
                case Symbol("]", _) :: afterClose => Success(arr, afterClose)
                case other :: _ =>
                  Failure(SyntaxReadException(s"Expected ',' or ']', found $other", other.pos))
                case Nil =>
                  Failure(new IllegalArgumentException("Unexpected EOF in array"))
              }
            case Failure(e) => Failure(e)
          }
      }
    }

    parseElements(tokens)
  }
}