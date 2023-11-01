import cn.bakamc.folia.config.Configs
import cn.bakamc.folia.db.database
import cn.bakamc.folia.db.table.PlayerInfo
import cn.bakamc.folia.db.table.playerInfos
import kotlinx.coroutines.*
import org.ktorm.entity.toList
import java.nio.file.Path
import kotlin.math.pow

fun main() {
    val a = 0.5454651231
    println(a.round(2))
}

fun Double.round(c: Int): Double {
    return Math.round( this * 10.0.pow(c.toDouble())) * 0.1.pow(c.toDouble())
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


