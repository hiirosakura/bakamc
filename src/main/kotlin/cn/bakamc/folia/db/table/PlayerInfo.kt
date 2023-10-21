package cn.bakamc.folia.db.table

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.varchar

open class PlayerInfos(alias: String?) : Table<PlayerInfo>("player", alias) {
    companion object : PlayerInfos(null)

    override fun aliased(alias: String): Table<PlayerInfo> = PlayerInfos(alias)

    val uuid = varchar("uuid").primaryKey().bindTo { it.uuid }
    val name = varchar("name").bindTo { it.name }
}

interface PlayerInfo : Entity<PlayerInfo> {
    companion object : Entity.Factory<PlayerInfo>()

    var uuid: String

    var name: String

}

val Database.playerInfos get() = this.sequenceOf(PlayerInfos)
