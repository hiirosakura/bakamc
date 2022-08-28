package cn.bakamc.common.config.modconfig.impl

import com.google.gson.JsonObject
import cn.bakamc.common.config.ConfigUtil
import cn.bakamc.common.utils.net.httpGet
import cn.bakamc.common.utils.net.httpPost
import cn.bakamc.common.utils.toJsonObject
import cn.bakamc.common.utils.toJsonStr

/**
 * 远程mod配置

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.modconfig.impl

 * 文件名 RemoteModConfig

 * 创建时间 2022/8/12 13:55

 * @author forpleuvoir

 */
abstract class RemoteModConfig(private val saveUrl: String, private val loadUrl: String) : AbstractModConfig() {


	override fun save() {
		ConfigUtil.run {
			val json = JsonObject()
			allCategory.forEach {
				writeConfigCategory(json, it)
			}
			httpPost(saveUrl, json.toJsonStr()).send().let {
				if (it.statusCode() != 200) {
					println("config save failed, url:%s,json:%s".format(saveUrl, json.toJsonStr()))
				} else {
					isChanged.set(false)
				}
			}
		}
	}

	override fun saveAsync() {
		ConfigUtil.run {
			val json = JsonObject()
			allCategory.forEach {
				writeConfigCategory(json, it)
			}
			httpPost(saveUrl, json.toJsonStr()).sendAsync {
				if (it.statusCode() != 200) {
					println("config save failed, url:%s,json:%s".format(saveUrl, json.toJsonStr()))
				} else {
					isChanged.set(false)
				}
			}
		}
	}

	override fun load() {
		httpGet(loadUrl).send().let {
			if (it.statusCode() != 200) {
				println("config load failed, url:%s,body:%s".format(saveUrl, it.body()))
			} else {
				it.body().toJsonObject().run {
					allCategory.forEach { config ->
						ConfigUtil.readConfigCategory(this, config)
					}
				}
			}
		}
	}


	override fun loadAsync() {
		httpGet(loadUrl).sendAsync {
			if (it.statusCode() != 200) {
				println("config load failed, url:%s,body:%s".format(saveUrl, it.body()))
			} else {
				it.body().toJsonObject().run {
					allCategory.forEach { config ->
						ConfigUtil.readConfigCategory(this, config)
					}
				}
			}
		}
	}
}