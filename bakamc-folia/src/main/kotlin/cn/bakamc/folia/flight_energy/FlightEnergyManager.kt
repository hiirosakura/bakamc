package cn.bakamc.folia.flight_energy

import cn.bakamc.folia.BakaMCPlugin
import moe.forpleuvoir.nebula.common.api.Initializable
import moe.forpleuvoir.nebula.common.util.second
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.*


object FlightEnergyManager : Listener, Initializable {
    override fun init() {
        Timer("[${BakaMCPlugin.name}]FlightEnergyManager")
                .schedule(
                    object : TimerTask() {
                        override fun run() {
                            tick()
                        }
                    }, Date(), 1.second
                )

    }

    /**
     * 玩家当前的飞行能量
     */
    val Player.energy: Double
        get() {


            return 0.0
        }


    /**
     * 由单独线程控制循环
     * 每秒执行一次
     */
    fun tick() {


    }


}