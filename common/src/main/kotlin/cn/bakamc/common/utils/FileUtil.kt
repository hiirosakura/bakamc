package cn.bakamc.common.utils

import java.io.*
import java.nio.charset.StandardCharsets

/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.utils

 * 文件名 FileUtil

 * 创建时间 2022/7/2 21:40

 * @author forpleuvoir

 */
object FileUtil {
	/**
	 * 加载文件中的内容
	 *
	 * @param path 文件路径
	 * @param name 文件名
	 * @return 内容
	 * @throws Exception [FileNotFoundException] 文件未找到
	 */
	@Throws(Exception::class)
	fun readFile(path: String, name: String): String {
		return readFile(File("$path/$name"))
	}

	/**
	 * 加载文件中的内容
	 *
	 * @param file 文件
	 * @return 内容
	 * @throws IOException [IOException] 文件未找到
	 */
	@Throws(IOException::class)
	fun readFile(file: File): String {
		if (!file.exists()) {
			throw FileNotFoundException(String.format("File %s not found:", file.name))
		}
		val result = StringBuilder()
		val bf = BufferedReader(
			InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8)
		)
		var s: String?
		while (bf.readLine().also { s = it } != null) {
			result.append(System.lineSeparator()).append(s)
		}
		bf.close()
		return result.toString()
	}

	/**
	 * 创建文件
	 *
	 * @param path 文件路径
	 * @param name 文件名
	 * @return 成功创建的文件对象
	 * @throws IOException 文件创建失败
	 */
	@Throws(IOException::class)
	fun createFile(path: String, name: String): File {
		val fileDir = File(path)
		if (!fileDir.exists()) {
			fileDir.mkdirs()
		}
		val file = File("$path/$name")
		if (!file.exists()) {
			file.createNewFile()
		}
		return file
	}

	/**
	 * 创建文件
	 *
	 * @param file 文件路径
	 * @return 成功创建的文件对象
	 * @throws IOException 文件创建失败
	 */
	@Throws(IOException::class)
	fun createFile(file: File): File {
		if (!file.parentFile.exists()) {
			file.parentFile.mkdirs()
			if (!file.exists()) {
				file.createNewFile()
			}
		}
		return file
	}

	/**
	 * 将字符串写入文件
	 *
	 * @param file    文件对象
	 * @param content 写入的内容
	 * @return 文件对象
	 * @throws Exception 文件写入失败
	 */
	@Throws(Exception::class)
	fun writeFile(file: File, content: String): File {
		return try {
			val fileWriter = OutputStreamWriter(FileOutputStream(file), StandardCharsets.UTF_8)
			fileWriter.write(content)
			fileWriter.close()
			file
		} catch (e: Exception) {
			createFile(file)
		}
	}

}