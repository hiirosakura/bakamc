package cn.bakamc.common.utils

import com.google.common.reflect.ClassPath

/**
 * 反射工具

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.utils

 * 文件名 ReflectionUtil

 * 创建时间 2022/8/6 13:58

 * @author forpleuvoir

 */
object ReflectionUtil {

	@JvmStatic
	fun getSuperClass(clazz: Class<*>): List<Class<*>> {
		val classes: MutableList<Class<*>> = ArrayList()
		var suCl = clazz.superclass
		while (suCl != null) {
			classes.add(suCl)
			suCl = if (suCl.superclass != Object::class.java) suCl.superclass else null
		}
		return classes
	}

	@JvmStatic
	fun isAssignableFrom(type: Class<*>, target: Class<*>): Boolean {
		return target.isAssignableFrom(type)
	}


	@JvmStatic
	fun getInterfaces(clazz: Class<*>): List<Class<*>> {
		val interfaces = clazz.interfaces
		return listOf(*interfaces)
	}

	@Suppress("UnstableApiUsage")
	@JvmStatic
	fun scanPackage(pack: String, predicate: (Class<*>) -> Boolean = { true }): List<Class<*>> {
		val list = ArrayList<Class<*>>()
		ClassPath.from(this::class.java.classLoader).getTopLevelClassesRecursive(pack).forEach {
			try {
				val clazz = Class.forName(it.name)
				if (predicate(clazz)) list.add(clazz)
			} catch (_: Exception) {
			}
		}
		return list
	}

}