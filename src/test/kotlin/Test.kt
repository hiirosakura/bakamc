import cn.bakamc.folia.config.Configs
import cn.bakamc.folia.db.database
import cn.bakamc.folia.db.table.PlayerInfo
import cn.bakamc.folia.db.table.playerInfos
import kotlinx.coroutines.*
import org.ktorm.entity.toList
import java.nio.file.Path

fun main() {
    val text = "{}第一段{}{}第二段{}"
    val params = arrayOf("1", "2", "3", "4")
    val result = StringBuilder()
    for ((index, str) in text.split("{}").withIndex()) {
        result.append(str)
        if (params.size - index >= 1) result.append(params[index])
    }
    println(result)
}

fun CoroutineScope.suspendFun(): Deferred<Int> {
    return async {
        delay(2000)
        println(Thread.currentThread().name)
        114514
    }
}

suspend fun Configs.testInit(path: Path) {
    configPath = path
    init()
    runCatching {
        generateTemp()
        Configs.load()
        if (this.needSave) {
            Configs.save()
        }
    }.onFailure {
        it.printStackTrace()
        Configs.forceSave()
    }
}

suspend fun suspendFun2() {
    database {
        playerInfos.toList()
    }
}


val DefaultScope = CoroutineScope(Dispatchers.Default)

suspend fun db() {
    Configs.init(Path.of("./build/config"))


    val players = DefaultScope.async { getAllPlayer() }
    println("我就不等他执行完了")

    runBlocking {
        players.await().forEach {
            println(it)
        }

    }

}

suspend fun getAllPlayer(): List<PlayerInfo> {
    return database {
        playerInfos.toList().subList(0, 50)
    }
}


