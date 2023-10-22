package cn.bakamc.folia.item

import cn.bakamc.folia.db.table.SpecialItem
import cn.bakamc.folia.service.SpecialItemService
import moe.forpleuvoir.nebula.common.api.Initializable
import java.util.concurrent.ConcurrentHashMap

object SpecialItemManager : Initializable {

    private lateinit var itemCache: MutableMap<String, SpecialItem>
    override fun init() {
        itemCache = ConcurrentHashMap()
        SpecialItemService.getSpecialItems().forEach {
            itemCache[it.key] = it
        }
    }

    fun onDisable() {
        if (this::itemCache.isInitialized) {
            itemCache.clear()
        }
    }

    fun put(specialItem: SpecialItem): Boolean {
        SpecialItemService.inertOrUpdate(specialItem)?.let {
            itemCache[it.key] = it
            return true
        }
        return false
    }

    fun remove(key: String): SpecialItem? {
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