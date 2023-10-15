package cn.bakamc.folia.service

import cn.bakamc.folia.db.entity.FlightEnergyVO
import cn.bakamc.folia.db.entity.PlayerVO
import cn.bakamc.folia.db.mapper.FlightEnergyMapper
import cn.bakamc.folia.db.mapper.PlayerMapper
import cn.bakamc.folia.extension.VO
import cn.bakamc.folia.extension.uuid
import cn.bakamc.folia.util.mapper
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import org.bukkit.entity.Player

object PlayerService {

    fun insertOrUpdate(player: Player) {
        mapper<PlayerMapper> {
            selectOne(QueryWrapper<PlayerVO>().eq("uuid", player.uuid))?.let {
                updateById(player.VO)
            } ?: insert(player.VO)
        }
    }


    fun getFlightEnergy(player: Player): Double {
        mapper<FlightEnergyMapper> {
            return selectOne(QueryWrapper<FlightEnergyVO>().eq("uuid", player.uuid))?.energy ?: run {
                insert(FlightEnergyVO(player.uuid, 0.0))
                0.0
            }
        }
    }

    fun getFlightEnergy(players: Collection<Player>): Map<Player, Double> {
        mapper<FlightEnergyMapper> {
            return buildMap {
                players.forEach { player ->
                    val energy = selectOne(QueryWrapper<FlightEnergyVO>().eq("uuid", player.uuid))?.energy ?: run {
                        insert(FlightEnergyVO(player.uuid, 0.0))
                        0.0
                    }
                    put(player, energy)
                }
            }
        }
    }

    fun updateFlightEnergy(player: Player, energy: Double) {
        mapper<FlightEnergyMapper> {
            update(FlightEnergyVO(player.uuid, energy), UpdateWrapper<FlightEnergyVO>().eq("uuid", player.uuid))
        }
    }

    fun updateFlightEnergy(flightEnergy: Map<Player, Double>) {
        mapper<FlightEnergyMapper> {
            flightEnergy.forEach { (player, energy) ->
                update(FlightEnergyVO(player.uuid, energy), UpdateWrapper<FlightEnergyVO>().eq("uuid", player.uuid))
            }
        }
    }

}