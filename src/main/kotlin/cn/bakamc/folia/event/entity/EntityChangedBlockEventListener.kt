package cn.bakamc.folia.event.entity

import cn.bakamc.folia.config.Configs.BLOCK_INFOS
import cn.bakamc.folia.config.Configs.Entity.CHANGE_BLOCK_MAP
import cn.bakamc.folia.config.Configs.Entity.ENTITY_INFOS
import cn.bakamc.folia.config.Configs.Entity.EXPLODE_BLOCK_MAP
import cn.bakamc.folia.util.logger
import cn.bakamc.folia.util.serialization
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.deserialization
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityExplodeEvent

object EntityChangedBlockEventListener : Listener {

    fun reloadCache() {
        changedCache = changedCacheSupplier()
        logger.info("重载实体更改方块缓存")
        explodeCache = explodeCacheSupplier()
        logger.info("重载实体爆炸破坏方块缓存")
    }

    private var changedCache: Map<EntityInfo, List<BlockChange>> = changedCacheSupplier()

    private var explodeCache: Map<EntityInfo, List<BlockInfo>> = explodeCacheSupplier()

    private fun changedCacheSupplier() = CHANGE_BLOCK_MAP.mapKeys { (key, _) ->
        EntityInfo.parse(key)
    }.mapValues { (e, b) ->
        if (e != EntityInfo.EMPTY) b.map { BlockChange.parse(it) }
        else emptyList()
    }.filter { (key, _) ->
        key != EntityInfo.EMPTY
    }

    private fun explodeCacheSupplier() = EXPLODE_BLOCK_MAP.mapKeys { (key, _) ->
        EntityInfo.parse(key)
    }.mapValues { (e, b) ->
        if (e != EntityInfo.EMPTY) b.map { BlockInfo.parse(it) }
        else emptyList()
    }.filter { (key, _) ->
        key != EntityInfo.EMPTY
    }

    private inline fun inChangedCache(entity: Entity, from: Block, to: String, action: () -> Unit) {
        changedCache.forEach { (e, b) ->
            if (e.isMatch(entity)) {
                b.find { it.isMatch(from, to) }?.let {
                    action()
                }
            }
        }
    }

    private inline fun inExplodeCache(entity: Entity, blocks: List<Block>, action: (Block) -> Unit) {
        explodeCache.forEach { (e, b) ->
            if (e.isMatch(entity)) {
                blocks.forEach { block ->
                    b.find { it.isMatch(block) }?.let {
                        action(block)
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun entityChangedBlockEvent(event: EntityChangeBlockEvent) {
        inChangedCache(event.entity, event.block, event.to.key.toString()) {
            event.isCancelled = true
            return
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun entityExplodeEvent(event: EntityExplodeEvent) {
        val list = ArrayList<Block>()
        inExplodeCache(event.entity, event.blockList()) {
            list.add(it)
        }
        event.blockList().removeAll(list)
    }

}

data class EntityInfo(
    val name: String? = null,
    val uuid: String? = null,
    val type: String? = null,
) : Serializable {

    fun isMatch(target: Entity): Boolean {
        if (this == EMPTY) return false
        return name?.let { target.name == it } ?: true
               && uuid?.let { target.uniqueId.toString() == it } ?: true
               && type?.let { target.type.key.toString() == it } ?: true
    }

    override fun serialization(): SerializeElement {
        return serializeObject {
            name?.let { "name" - it }
            uuid?.let { "uuid" - it }
            type?.let { "type" - it }
        }
    }

    companion object : Deserializer<EntityInfo> {

        val EMPTY by lazy { EntityInfo() }

        fun parse(string: String): EntityInfo {
            return ENTITY_INFOS[string] ?: if (string.isNotEmpty()) EntityInfo(type = string) else EMPTY
        }

        override fun deserialization(serializeElement: SerializeElement): EntityInfo {
            serializeElement as SerializeObject
            return EntityInfo(
                serializeElement["name"]?.asString,
                serializeElement["uuid"]?.asString,
                serializeElement["type"]?.asString,
            )
        }
    }

}


data class BlockChange(
    val from: BlockInfo? = null,
    val to: BlockInfo? = null,
) {
    companion object {

        val EMPTY by lazy { BlockChange() }

        private const val SYMBOL = "->"

        fun parse(string: String): BlockChange {
            if (string.contains(SYMBOL)) {
                string.replace(" ", "").split(SYMBOL).apply {
                    val from = if (this[0].isNotEmpty()) BLOCK_INFOS[this[0]] ?: BlockInfo(type = this[0]) else null
                    val to = if (this[1].isNotEmpty()) BLOCK_INFOS[this[1]] ?: BlockInfo(type = this[1]) else null
                    return BlockChange(from, to)
                }
            } else {
                return if (string.isNotEmpty()) BlockChange(BLOCK_INFOS[string] ?: BlockInfo(type = string)) else EMPTY
            }
        }
    }

    fun isMatch(from: Block, to: String): Boolean {
        var result = false
        if (this.from != null) result = this.from.isMatch(from)
        if (this.to != null) result = this.to.type == to
        return result
    }

}

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
        return x?.let { block.x in it } ?: true
               && y?.let { block.y in it } ?: true
               && z?.let { block.z in it } ?: true
               && type?.let { block.blockData.material.key.toString() == it } ?: true
               && biome?.let { block.biome.key.toString() == it } ?: true
               && world?.let { block.world.key.toString() == it } ?: true
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
            return BLOCK_INFOS[string] ?: if (string.isNotEmpty()) BlockInfo(type = string) else EMPTY
        }

        override fun deserialization(serializeElement: SerializeElement): BlockInfo {
            serializeElement as SerializeObject
            return BlockInfo(
                serializeElement["x"]?.let { IntRange.deserialization(it) },
                serializeElement["y"]?.let { IntRange.deserialization(it) },
                serializeElement["z"]?.let { IntRange.deserialization(it) },
                serializeElement["type"]?.asString,
                serializeElement["biome"]?.asString,
                serializeElement["world"]?.asString,
            )
        }

    }

}