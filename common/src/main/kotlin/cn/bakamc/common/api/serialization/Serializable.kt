package cn.bakamc.common.api.serialization

/**
 *
 * 可序列化

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.api.serialization

 * 文件名 Serializable

 * 创建时间 2022/7/2 22:51

 * @author forpleuvoir

 */
interface Serializable<T> {

	val serialization: T

	fun deserialize(serializedObject: T){
		throw NotImplementedError("method not implemented")
	}

}