package cn.bakamc.common.config

import cn.bakamc.common.config.modconfig.ConfigCategory
import cn.bakamc.common.utils.getNestedObject
import cn.bakamc.common.utils.getOr
import cn.bakamc.common.utils.gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.util.*

/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config

 * 文件名 ConfigUtil

 * 创建时间 2022/8/12 0:07

 * @author forpleuvoir

 */
object ConfigUtil {

	fun configFile(configFileName: String, path: Path, create: Boolean = true): File {
		val file = File(path.toFile(), "${configFileName}.json")
		if (!file.exists() && create) {
			file.createNewFile()
		}
		return file
	}

	fun writeJsonToFile(root: JsonObject, file: File): Boolean {
		var fileTmp = File(file.parentFile, file.name + ".tmp")
		if (fileTmp.exists()) {
			fileTmp = File(file.parentFile, UUID.randomUUID().toString() + ".tmp")
		}
		try {
			OutputStreamWriter(FileOutputStream(fileTmp), StandardCharsets.UTF_8).use { writer ->
				writer.write(gson.toJson(root))
				writer.close()
				if (file.exists() && file.isFile && !file.delete()) {
					println("Failed to delete file '%s'".format(file.absolutePath))
				}
				return fileTmp.renameTo(file)
			}
		} catch (e: Exception) {
			println("Failed to write JSON data to file '%s'".format(fileTmp.absolutePath))
			e.printStackTrace()
		}
		return false
	}

	fun writeConfigCategory(root: JsonObject, category: ConfigCategory) {
		root.getNestedObject(category.name, true)?.let {
			for (config in category.allConfigs) {
				it.add(config.key, config.serialization)
			}
		}
	}

	fun readConfigCategory(root: JsonObject, category: ConfigCategory) {
		val obj = root.getOr(category.name, JsonObject())
		if (!obj.entrySet().isNullOrEmpty()) {
			category.allConfigs.forEach {
				if (obj.has(it.key)) {
					it.deserialize(obj[it.key])
				}
			}
		}
	}

	fun paresJsonFile(file: File, action: (JsonElement) -> Unit) {
		if (file.exists() && file.isFile && file.canRead()) {
			val fileName = file.absolutePath
			try {
				InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8).use { reader ->
					JsonParser.parseReader(reader).run {
						if (!this.isJsonNull)
							action(this)
					}
				}
			} catch (e: Exception) {
				println("Failed to parse the JSON file '%s'".format(fileName))
				e.printStackTrace()
			}
		}
	}


}