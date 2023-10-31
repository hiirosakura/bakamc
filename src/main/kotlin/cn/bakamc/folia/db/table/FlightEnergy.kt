package cn.bakamc.folia.db.table

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.double
import org.ktorm.schema.varchar

open class FlightEnergies(alias: String?) : Table<FlightEnergy>("flight_energy", alias) {
    companion object : FlightEnergies(null)

    override fun aliased(alias: String): Table<FlightEnergy> = FlightEnergies(alias)

    val uuid = varchar("uuid").primaryKey().references(PlayerInfos) { it.player }

    val energy = double("energy").bindTo { it.energy }

    val enabled = boolean("enabled").bindTo { it.enabled }

    val barVisible = boolean("bar_visible").bindTo { it.barVisible }
}

interface FlightEnergy : Entity<FlightEnergy> {
    companion object : Entity.Factory<FlightEnergy>()

    var player: PlayerInfo

    var energy: Double

    var enabled: Boolean

    var barVisible: Boolean

    val uuid: String get() = player.uuid
}

val Database.flightEnergies get() = this.sequenceOf(FlightEnergies)