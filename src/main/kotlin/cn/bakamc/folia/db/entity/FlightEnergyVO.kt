package cn.bakamc.folia.db.entity

import cn.bakamc.folia.util.NoArg
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.util.*

@NoArg
@TableName("flight_energy")
data class FlightEnergyVO(
    @TableId("uuid")
    val uuid: String,
    @TableField("energy")
    val energy: Double
){
    fun uuid(): UUID {
        return UUID.fromString(uuid)
    }

}