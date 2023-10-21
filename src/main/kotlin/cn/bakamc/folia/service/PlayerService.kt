package cn.bakamc.folia.service

import cn.bakamc.folia.db.database
import cn.bakamc.folia.db.table.*
import cn.bakamc.folia.extension.uuid
import org.bukkit.entity.Player
import org.ktorm.dsl.batchUpdate
import org.ktorm.dsl.eq
import org.ktorm.dsl.inList
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.forEach
import org.ktorm.support.mysql.insertOrUpdate

object PlayerService {

    fun insertOrUpdate(player: Player) {
        database.insertOrUpdate(PlayerInfos) {
            set(it.uuid, player.uuid)
            set(it.name, player.name)
            onDuplicateKey {
                set(it.name, player.name)
            }
        }
    }


    fun getFlightEnergy(player: Player): Double {
        return database {
            flightEnergies.find {
                val playerInfo = (it.uuid.referenceTable as PlayerInfos)
                playerInfo.uuid eq player.uuid
            }?.energy ?: run {
                flightEnergies.add(FlightEnergy {
                    this.player = playerInfos.find { it.uuid eq player.uuid }!!
                    energy = 0.0
                })
                0.0
            }
        }
    }

    fun getFlightEnergies(players: Collection<Player>): Map<Player, Double> {
        if (players.isEmpty()) return emptyMap()
        return database {
            buildMap {
                flightEnergies.filter { flightEnergy ->
                    flightEnergy.uuid inList players.map { it.uuid }
                }.forEach {
                    this[players.find { player -> player.uuid == it.uuid }!!] = it.energy
                }
            }
        }
    }

    fun updateFlightEnergy(player: Player, energy: Double) {
        database {
            flightEnergies.filter {
                it.uuid eq player.uuid
            }.forEach {
                it.energy = energy
                it.flushChanges()
            }
        }
    }

    fun updateFlightEnergies(flightEnergy: Map<Player, Double>): Int {
        if (flightEnergy.isEmpty()) return 0
        return database {
            batchUpdate(FlightEnergies) {
                flightEnergy.forEach { (k, v) ->
                    item {
                        set(it.energy, v)
                        where {
                            it.uuid eq k.uuid
                        }
                    }
                }
            }
        }.filter { it > 0 }.size
    }

}