package cn.bakamc.common.utils

import cn.bakamc.common.api.serialization.JsonSerializer
import com.google.gson.*

/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.utils

 * 文件名 JsonUtil

 * 创建时间 2022/7/2 21:43

 * @author forpleuvoir

 */

val gson: Gson by lazy { GsonBuilder().setPrettyPrinting().create() }

val String.parseToJsonArray: JsonArray get() = JsonParser.parseString(this).asJsonArray

val String.parseToJsonObject: JsonObject get() = JsonParser.parseString(this).asJsonObject

val String.parseToJsonElement: JsonElement get() = JsonParser.parseString(this)

val JsonElement.string: String get() = this.toString()

fun Any.toJsonObject(): JsonObject {
	return gson.toJsonTree(this).asJsonObject
}

fun JsonObject.getNestedObject(key: String, create: Boolean = false): JsonObject? {
	return if (!this.has(key) || this[key].isJsonObject) {
		if (!create) {
			return null
		}
		val obj = JsonObject()
		this.add(key, obj)
		obj
	} else {
		this[key].asJsonObject
	}
}

/**
 * 将对象转换成json字符串
 *
 * @return json字符串
 */
fun Any.toJsonStr(): String {
	return gson.toJson(this)
}

fun jsonArray(vararg elements: Any): JsonArray {
	return jsonArray(elements.toList())
}

inline fun <T> jsonArray(iterable: Iterable<T>, converter: (T) -> JsonElement): JsonArray {
	return JsonArray().apply {
		for (t in iterable) {
			add(converter(t))
		}
	}
}

fun jsonArray(elements: Iterable<Any>): JsonArray {
	return JsonArray().apply {
		for (element in elements) {
			when (element) {
				is Boolean        -> add(element)
				is Number         -> add(element)
				is String         -> add(element)
				is Char           -> add(element)
				is JsonElement    -> add(element)
				is JsonSerializer -> add(element.serialization)
				else              -> add(element.toJsonObject())
			}
		}
	}
}

class JsonObjectScope {

	internal val jsonObject: JsonObject = JsonObject()

	infix fun String.at(value: String) {
		jsonObject.addProperty(this, value)
	}

	infix fun String.at(value: Number) {
		jsonObject.addProperty(this, value)
	}

	infix fun String.at(value: Boolean) {
		jsonObject.addProperty(this, value)
	}

	infix fun String.at(value: Char) {
		jsonObject.addProperty(this, value)
	}

	infix fun String.at(value: JsonElement) {
		jsonObject.add(this, value)
	}
}

fun jsonObject(scope: JsonObjectScope.() -> Unit): JsonObject {
	val jsonObjectScope = JsonObjectScope()
	jsonObjectScope.scope()
	return jsonObjectScope.jsonObject
}

fun jsonObject(map: Map<String, Any>): JsonObject {
	return jsonObject {
		map.forEach { (key, element) ->
			when (element) {
				is Boolean     -> key at element
				is Number      -> key at element
				is String      -> key at element
				is Char        -> key at element
				is JsonElement -> key at element
				else           -> key at element.toJsonObject()
			}
		}
	}
}

fun <T> jsonObject(map: Map<String, T>, converter: (T) -> JsonElement): JsonObject {
	return jsonObject {
		for (entry in map) {
			entry.key at converter(entry.value)
		}
	}
}

inline fun <reified T> JsonObject.getOr(key: String, or: T): T {
	this.has(key).ifc {
		try {
			return gson.fromJson(this.get(key), T::class.java)
		} catch (_: Exception) {
		}
	}
	return or
}

fun JsonObject.getOr(key: String, or: Number): Number {
	this.has(key).ifc {
		try {
			return this.get(key).asNumber
		} catch (_: Exception) {
		}
	}
	return or
}

fun JsonObject.getOr(key: String, or: Boolean): Boolean {
	this.has(key).ifc {
		try {
			return this.get(key).asBoolean
		} catch (_: Exception) {
		}
	}
	return or
}

fun JsonObject.getOr(key: String, or: String): String {
	this.has(key).ifc {
		try {
			return this.get(key).asString
		} catch (_: Exception) {
		}
	}
	return or
}

fun JsonObject.getOr(key: String, or: JsonObject): JsonObject {
	this.has(key).ifc {
		try {
			return this.get(key).asJsonObject
		} catch (_: Exception) {
		}
	}
	return or
}

fun JsonObject.getOr(key: String, or: JsonArray): JsonArray {
	this.has(key).ifc {
		try {
			return this.get(key).asJsonArray
		} catch (_: Exception) {
		}
	}
	return or
}