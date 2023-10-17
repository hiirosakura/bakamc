package cn.bakamc.folia.db.entity

import cn.bakamc.folia.util.NoArg
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@NoArg
@TableName("flight_energy")
data class FlightEnergyVO(
    @TableId("uuid")
    val uuid: String,
    @TableField("energy")
    val energy: Double
)