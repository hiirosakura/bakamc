package moe.forpleuvoir.nebula.serialization.json

import moe.forpleuvoir.nebula.serialization.ast.{SyntaxDecoder, Token}
import moe.forpleuvoir.nebula.serialization.base.*

import scala.util.{Failure, Success, Try}

object JsonDecoder extends SyntaxDecoder {

  override def decode(tokens: List[Token]): Try[SerializeElement] = {
    if (tokens.isEmpty || tokens.head == Token.EOF) {
      Failure(new IllegalArgumentException("Empty token stream"))
    } else {
      parseElement(tokens).map { (element, remaining) =>
        if (remaining.nonEmpty && remaining.head != Token.EOF) {
          throw new IllegalArgumentException(s"Unexpected token after expression: ${remaining.head}")
        }
        element
      }
    }
  }

  private def parseElement(tokens: List[Token]): Try[(SerializeElement, List[Token])] = {
    tokens match {
      case Token.Symbol("{") :: tail => parseObject(tail)
      case Token.Symbol("[") :: tail => parseArray(tail)
      case Token.Literal(value) :: tail =>
        Success(value match {
          case p: Primitive => SerializePrimitive(p)
          case null => SerializeNull
        }, tail)
      case head :: _ =>
        Failure(new IllegalArgumentException(s"Unexpected token: $head"))
      case Nil =>
        Failure(new IllegalArgumentException("Unexpected end of input"))
    }
  }

  private def parseObject(tokens: List[Token]): Try[(SerializeObject, List[Token])] = {
    val obj = SerializeObject()

    def parseMembers(currentTokens: List[Token]): Try[(SerializeObject, List[Token])] = {
      currentTokens match {
        case Token.Symbol("}") :: tail => Success(obj, tail)
        case Token.Literal(key: String) :: Token.Symbol(":") :: tail =>
          parseElement(tail).flatMap { case (value, nextTail) =>
            obj.put(key, value)
            nextTail match {
              case Token.Symbol(",") :: afterComma => parseMembers(afterComma)
              case Token.Symbol("}") :: afterClose => Success(obj, afterClose)
              case other => Failure(new IllegalArgumentException(s"Expected ',' or '}', found $other"))
            }
          }
        case other => Failure(new IllegalArgumentException(s"Expected key or '}', found $other"))
      }
    }

    parseMembers(tokens)
  }

  private def parseArray(tokens: List[Token]): Try[(SerializeArray, List[Token])] = {
    val arr = SerializeArray()

    def parseElements(currentTokens: List[Token]): Try[(SerializeArray, List[Token])] = {
      currentTokens match {
        case Token.Symbol("]") :: tail => Success(arr, tail)
        case _ =>
          parseElement(currentTokens).flatMap { case (element, nextTail) =>
            arr.addOne(element)
            nextTail match {
              case Token.Symbol(",") :: afterComma => parseElements(afterComma)
              case Token.Symbol("]") :: afterClose => Success(arr, afterClose)
              case other => Failure(new IllegalArgumentException(s"Expected ',' or ']', found $other"))
            }
          }
      }
    }

    parseElements(tokens)
  }

}
