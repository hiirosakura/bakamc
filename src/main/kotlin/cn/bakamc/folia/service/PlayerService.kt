package cn.bakamc.folia.service

import cn.bakamc.folia.db.database
import cn.bakamc.folia.db.table.*
import cn.bakamc.folia.extension.uuid
import org.bukkit.entity.Player
import org.ktorm.dsl.batchUpdate
import org.ktorm.dsl.eq
import org.ktorm.dsl.inList
import org.ktorm.entity.*
import org.ktorm.support.mysql.insertOrUpdate

object PlayerService {

    suspend fun insertOrUpdate(player: Player) {
        database {
            insertOrUpdate(PlayerInfos) {
                set(it.uuid, player.uuid)
                set(it.name, player.name)
                onDuplicateKey {
                    set(it.name, player.name)
                }
            }
        }
    }


    suspend fun getFlightEnergy(player: Player): FlightEnergy {
        return database {
            flightEnergies.find {
                val playerInfo = (it.uuid.referenceTable as PlayerInfos)
                playerInfo.uuid eq player.uuid
            } ?: run {
                val flightEnergy = FlightEnergy {
                    this.player = playerInfos.find { it.uuid eq player.uuid }!!
                    enabled = false
                    energy = 0.0
                    barVisible = true
                }
                flightEnergies.add(flightEnergy)
                flightEnergy
            }
        }
    }

    suspend fun getFlightEnergies(players: Collection<Player>): Map<Player, FlightEnergy> {
        if (players.isEmpty()) return emptyMap()
        return database {
            buildMap {
                flightEnergies.filter { flightEnergy ->
                    flightEnergy.uuid inList players.map { it.uuid }
                }.forEach {
                    this[players.find { player -> player.uuid == it.uuid }!!] = it
                }
            }
        }
    }

    suspend fun updateFlightEnergy(energy: FlightEnergy) {
        database {
            flightEnergies.update(energy)
        }
    }

    suspend fun updateFlightEnergies(flightEnergy: Collection<FlightEnergy>): Int {
        if (flightEnergy.isEmpty()) return 0
        return database {
            batchUpdate(FlightEnergies) {
                flightEnergy.forEach { e ->
                    item {
                        set(it.energy, e.energy)
                        set(it.enabled, e.enabled)
                        set(it.barVisible, e.barVisible)
                        where {
                            it.uuid eq e.uuid
                        }
                    }
                }
            }
        }.filter { it > 0 }.size
    }

}