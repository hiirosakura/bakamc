package cn.bakamc.folia.hook.placeholder

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.hook.BakaMCHook
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

object Placeholder : BakaMCHook {

    private lateinit var hook: Any

    override fun onEnable(plugin: BakaMCPlugin) {
        hook = object : PlaceholderExpansion() {

            override fun getIdentifier(): String = "bakamc"

            override fun getAuthor(): String = "forpleuvoir"

            override fun getVersion(): String = BakaMCPlugin.instance.bakaVersion

            override fun canRegister(): Boolean = true

            override fun onPlaceholderRequest(player: Player?, params: String): String? {
                return BakamcPlaceholderExpansion.expansions.find { it.name == params }?.onRequest(player)
            }

        }.apply {
            register()
        }
    }

    override fun onDisable(plugin: BakaMCPlugin) {
        (hook as? PlaceholderExpansion)?.unregister()
    }

}


interface BakamcPlaceholderExpansion {

    companion object {

        internal val expansions by lazy {
            listOf<BakamcPlaceholderExpansion>(

            )
        }
    }

    val name: String

    fun onRequest(player: Player?): String?

}