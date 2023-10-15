package cn.bakamc.folia.db.entity

import cn.bakamc.folia.util.NoArg
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@NoArg
@TableName("player")
data class PlayerVO(
    @TableId("uuid", type = IdType.UUID)
    val uuid: String,
    @TableField("name")
    val name: String
)