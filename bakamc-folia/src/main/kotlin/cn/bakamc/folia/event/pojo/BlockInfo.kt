package cn.bakamc.folia.event.pojo

import cn.bakamc.folia.config.BlockConfig
import cn.bakamc.folia.util.serialization
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.deserialization
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import org.bukkit.block.Block

data class BlockInfo(
    val x: IntRange? = null,
    val y: IntRange? = null,
    val z: IntRange? = null,
    val type: String? = null,
    val biome: String? = null,
    val world: String? = null,
) : Serializable {

    fun isMatch(block: Block): Boolean {
        if (this == EMPTY) return false
        return x?.let { block.x in it } != false
                && y?.let { block.y in it } != false
                && z?.let { block.z in it } != false
                && type?.let { block.blockData.material.key.toString() == it } != false
                && biome?.let { block.biome.key.toString() == it } != false
                && world?.let { block.world.key.toString() == it } != false
    }

    override fun serialization(): SerializeElement {
        return serializeObject {
            x?.let { "x" - it.serialization() }
            y?.let { "y" - it.serialization() }
            z?.let { "z" - it.serialization() }
            type?.let { "type" - it }
            biome?.let { "biome" - it }
            world?.let { "world" - it }
        }
    }

    companion object : Deserializer<BlockInfo> {

        val EMPTY by lazy { BlockInfo() }

        fun parse(string: String): BlockInfo {
            return BlockConfig.BLOCK_INFOS[string] ?: if (string.isNotEmpty()) BlockInfo(type = string) else EMPTY
        }

        fun fromBlock(block: Block): BlockInfo =
            BlockInfo(
                block.x..block.x,
                block.y..block.y,
                block.z..block.z,
                block.blockData.material.key.toString(),
                block.biome.key.toString(),
                block.world.key.toString(),
            )


        override fun deserialization(serializeElement: SerializeElement): BlockInfo {
            return serializeElement.checkType<SerializeObject, BlockInfo> { obj ->
                BlockInfo(
                    obj["x"]?.let { IntRange.deserialization(it) },
                    obj["y"]?.let { IntRange.deserialization(it) },
                    obj["z"]?.let { IntRange.deserialization(it) },
                    obj["type"]?.asString,
                    obj["biome"]?.asString,
                    obj["world"]?.asString,
                )
            }.getOrThrow()
        }

    }

}