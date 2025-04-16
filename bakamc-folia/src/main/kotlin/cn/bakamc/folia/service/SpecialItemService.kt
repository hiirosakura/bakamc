package cn.bakamc.folia.service

import cn.bakamc.folia.db.database
import cn.bakamc.folia.db.table.SpecialItem
import cn.bakamc.folia.db.table.SpecialItems
import cn.bakamc.folia.db.table.specialItems
import org.ktorm.dsl.batchUpdate
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.map
import org.ktorm.support.mysql.insertOrUpdate

object SpecialItemService {

    suspend fun getSpecialItems(): List<SpecialItem> {
        return database {
            specialItems.map { it }
        }
    }

    suspend fun getItemByKey(key: String): SpecialItem? {
        return database {
            specialItems.find { it.key eq key }
        }
    }

    suspend fun inertOrUpdate(specialItem: SpecialItem): SpecialItem? {
        database {
            insertOrUpdate(SpecialItems) {
                set(it.key, specialItem.key)
                set(it.id, specialItem.id)
                set(it.itemData, specialItem.itemData)
                onDuplicateKey {
                    set(it.id, specialItem.id)
                    set(it.itemData, specialItem.itemData)
                }
            }
        }.takeIf { it > 0 }?.let {
            return specialItem
        }
        return null
    }

    suspend fun delete(key: String): Int {
        return database {
            delete(SpecialItems) {
                it.key eq key
            }
        }
    }

    suspend fun updates(specialItems: List<SpecialItem>): Int {
        if (specialItems.isEmpty()) return 0
        return database {
            batchUpdate(SpecialItems) {
                for (specialItem in specialItems) {
                    item {
                        set(it.id, specialItem.id)
                        set(it.itemData, specialItem.itemData)
                        where {
                            it.key eq specialItem.key
                        }
                    }
                }
            }
        }.filter { it > 0 }.size
    }

}