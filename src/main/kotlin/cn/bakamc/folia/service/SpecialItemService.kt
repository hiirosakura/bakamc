package cn.bakamc.folia.service

import cn.bakamc.folia.db.database
import cn.bakamc.folia.db.table.SpecialItem
import cn.bakamc.folia.db.table.SpecialItems
import cn.bakamc.folia.db.table.specialItems
import org.ktorm.dsl.batchUpdate
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.entity.map
import org.ktorm.support.mysql.insertOrUpdate

object SpecialItemService {

    fun getSpecialItems(): List<SpecialItem> {
        return database {
            specialItems.map { it }
        }
    }

    fun inertOrUpdate(specialItem: SpecialItem): SpecialItem? {
        database.insertOrUpdate(SpecialItems) {
            set(it.key, specialItem.key)
            set(it.id, specialItem.id)
            set(it.nbtTag, specialItem.nbtTag)
            onDuplicateKey {
                set(it.id, specialItem.id)
                set(it.nbtTag, specialItem.nbtTag)
            }
        }.takeIf { it > 0 }?.let {
            return specialItem
        }
        return null
    }

    fun delete(key: String): Int {
        return database {
           delete(SpecialItems) {
               it.key eq key
           }
        }
    }

    fun updates(specialItems: List<SpecialItem>): Int {
        if (specialItems.isEmpty()) return 0
        return database {
            batchUpdate(SpecialItems) {
                for (specialItem in specialItems) {
                    item {
                        set(it.id, specialItem.id)
                        set(it.nbtTag, specialItem.nbtTag)
                        where {
                            it.key eq specialItem.key
                        }
                    }
                }
            }
        }.filter { it > 0 }.size
    }

}