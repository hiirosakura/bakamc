package cn.bakamc.common

interface BakaMcApp<S> {

	/**
	 * 初始化应用
	 * @param server S
	 */
	fun init(server: S)

	/**
	 * 重新加载应用
	 * @param server S
	 */
	fun reload(server: S)

	/**
	 * 关闭应用
	 * @param server S
	 */
	fun close(server: S)

}