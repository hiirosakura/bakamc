package cn.bakamc.common.config.modconfig.impl

import cn.bakamc.common.api.impl.ServerSavable

/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.modconfig.impl

 * 文件名 RemoteServerModConfig

 * 创建时间 2022/8/12 15:33

 * @author forpleuvoir

 */
abstract class RemoteServerModConfig<S>(
	override val modId: String,
	saveUrl: String,
	loadUrl: String,
) : RemoteModConfig(saveUrl, loadUrl), ServerSavable<S> {

	override fun init(server: S) {
		this.server = server
		allCategory.forEach { configCategory ->
			configCategory.allConfigs.forEach {
				it.resetDefValue()
			}
		}
		init()
	}

}