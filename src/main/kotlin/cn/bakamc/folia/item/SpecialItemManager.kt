package cn.bakamc.folia.item

import cn.bakamc.folia.db.table.SpecialItem
import cn.bakamc.folia.service.SpecialItemService
import cn.bakamc.folia.util.launch
import cn.bakamc.folia.util.logger
import moe.forpleuvoir.nebula.common.api.Initializable
import java.util.concurrent.ConcurrentHashMap

object SpecialItemManager : Initializable {

    private var itemCache: MutableMap<String, SpecialItem> = ConcurrentHashMap()

    override fun init() {
        launch {
            SpecialItemService.getSpecialItems().forEach {
                itemCache[it.key] = it
            }
            logger.info("特殊物品加载完成")
        }
    }

    fun onDisable() {
        itemCache.clear()
    }

    fun getCachedItem(key: String): SpecialItem? {
        return itemCache[key].apply {
            if (this == null) {
                launch {
                    SpecialItemService.getItemByKey(key)?.let {
                        itemCache[it.key] = it
                    }
                }
            }
        }
    }

    fun getCache(): Map<String, SpecialItem> {
        return itemCache
    }

    suspend fun put(specialItem: SpecialItem): Boolean {
        SpecialItemService.inertOrUpdate(specialItem)?.let {
            itemCache[it.key] = it
            return true
        }
        return false
    }

    suspend fun remove(key: String): SpecialItem? {
        return SpecialItemService.delete(key).takeIf { it > 0 }?.let {
            itemCache.remove(key)
        }
    }

    fun specifyType(keys: Set<String>): Map<String, SpecialItem> {
        return buildMap {
            keys.forEach {
                itemCache[it]?.let { specialItem ->
                    put(specialItem.key, specialItem)
                }
            }
        }
    }

}