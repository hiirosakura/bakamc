package moe.forpleuvoir.nebula.serialization.codec

import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.nebula.serialization.extension.SerObjectOps.*
import moe.forpleuvoir.nebula.serialization.extension.buildSerObject

import java.lang.reflect.{Field, Method}
import scala.collection.mutable
import scala.reflect.ClassTag
import scala.util.Try

/**
 * 只适用于简单的JavaBean
 */
class JavaBeanCodec[T](using tag: ClassTag[T]) extends Codec[T] {

  private val clazz: Class[T] = tag.runtimeClass.asInstanceOf[Class[T]]
  private val fields: List[Field] = getSerializableFields
  private val fieldGetters: Map[String, Method | Null] = fields.map(field => field.getName -> getGetterMethod(field)).toMap
  private val fieldSetters: Map[String, Method | Null] = fields.map(field => field.getName -> getSetterMethod(field)).toMap
  private val fieldCodecs: Map[String, Codec[?]] = fields.map(field => field.getName -> getFieldCodec(field)).toMap

  private def getSerializableFields: List[Field] = {
    // 收集所有非静态、非 transient 的字段
    val fields = mutable.ListBuffer[Field]()
    var currentClass: Class[?] = clazz
    while (currentClass != null && currentClass != classOf[Object]) {
      currentClass.getDeclaredFields.foreach {
        field =>
          if (!java.lang.reflect.Modifier.isStatic(field.getModifiers) && !java.lang.reflect.Modifier.isTransient(field.getModifiers)) {
            field.setAccessible(true)
            fields += field
          }
      }
      currentClass = currentClass.getSuperclass
    }
    fields.toList
  }

  private def getGetterMethod(field: Field): Method | Null = {
    val fieldName = field.getName
    val getterName = "get" + fieldName.substring(0, 1).toUpperCase + fieldName.substring(1)
    try {
      clazz.getMethod(getterName)
    } catch {
      case _: NoSuchMethodException =>
        // 对于 boolean 类型，尝试 is 前缀
        if (field.getType == classOf[Boolean]) {
          val isGetterName = "is" + fieldName.substring(0, 1).toUpperCase + fieldName.substring(1)
          try {
            clazz.getMethod(isGetterName)
          } catch {
            case _: NoSuchMethodException =>
              // 如果没有 getter 方法，直接使用字段访问
              field.setAccessible(true)
              null
          }
        } else {
          // 如果没有 getter 方法，直接使用字段访问
          field.setAccessible(true)
          null
        }
    }
  }

  private def getSetterMethod(field: Field): Method | Null = {
    val fieldName = field.getName
    val setterName = "set" + fieldName.substring(0, 1).toUpperCase + fieldName.substring(1)
    try {
      clazz.getMethod(setterName, field.getType)
    } catch {
      case _: NoSuchMethodException =>
        // 如果没有 setter 方法，直接使用字段访问
        field.setAccessible(true)
        null
    }
  }

  private def getFieldCodec(field: Field): Codec[?] = {
    import moe.forpleuvoir.nebula.serialization.codec.PrimitiveCodec.*
    field.getType match {
      case t if t == classOf[Char] => Char.asInstanceOf[Codec[?]]
      case t if t == classOf[String] => String.asInstanceOf[Codec[?]]
      case t if t == classOf[Int] => Int.asInstanceOf[Codec[?]]
      case t if t == classOf[Long] => Long.asInstanceOf[Codec[?]]
      case t if t == classOf[Float] => Float.asInstanceOf[Codec[?]]
      case t if t == classOf[Double] => Double.asInstanceOf[Codec[?]]
      case t if t == classOf[Boolean] => Boolean.asInstanceOf[Codec[?]]
      case t if t == classOf[Byte] => Byte.asInstanceOf[Codec[?]]
      case t if t == classOf[Short] => Short.asInstanceOf[Codec[?]]
      case t if t == classOf[BigInt] => BigInt.asInstanceOf[Codec[?]]
      case t if t == classOf[BigDecimal] => BigDecimal.asInstanceOf[Codec[?]]
      case t if t.isEnum => JavaEnumCodec.asInstanceOf[Codec[?]]
      case _ =>
        // 对于其他类型，尝试使用派生的 Codec
        // 注意：这只适用于 Scala 类型，对于 Java 类型可能需要特殊处理
        summon[Codec[Any]].asInstanceOf[Codec[?]]
    }
  }

  private def getFieldValue(obj: T, fieldName: String): Any = {
    fieldGetters.get(fieldName) match {
      case Some(getter) if getter != null => getter.invoke(obj)
      case _ => fields.find(_.getName == fieldName).get.get(obj)
    }
  }

  private def setFieldValue(obj: T, fieldName: String, value: Any): Unit = {
    fieldSetters.get(fieldName) match {
      case Some(setter) if setter != null => setter.invoke(obj, value)
      case _ => fields.find(_.getName == fieldName).get.set(obj, value)
    }
  }

  override def serialization(value: T): SerializeElement = {
    buildSerObject {
      fields.foreach { field =>
        val fieldName = field.getName
        val fieldValue = getFieldValue(value, fieldName)
        fieldCodecs.get(fieldName) match {
          case Some(codec) =>
            val serializedValue = codec.asInstanceOf[Codec[Any]].serialization(fieldValue)
            fieldName := serializedValue
          case None =>
          // 如果没有找到对应的 Codec，跳过该字段
        }
      }
    }
  }

  override def deserialization(data: SerializeElement): Try[T] = Try {
    val obj = clazz.getDeclaredConstructor().newInstance()
    val serializeObject = data.asObject.get

    fields.foreach {
      field =>
        val fieldName = field.getName
        if (serializeObject.contains(fieldName)) {
          val fieldData = serializeObject(fieldName)
          fieldCodecs.get(fieldName) match {
            case Some(codec) =>
              val deserializedValue = codec.deserialization(fieldData).get
              setFieldValue(obj, fieldName, deserializedValue)
            case None =>
            // 如果没有找到对应的 Codec，跳过该字段
          }
        }
    }

    obj
  }

}

object JavaBeanCodec {

  def apply[T](using tag: ClassTag[T]): JavaBeanCodec[T] = new JavaBeanCodec[T]

}
