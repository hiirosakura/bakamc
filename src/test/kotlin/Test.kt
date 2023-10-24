import cn.bakamc.folia.config.Configs
import cn.bakamc.folia.db.database
import cn.bakamc.folia.db.table.PlayerInfo
import cn.bakamc.folia.db.table.PlayerInfos
import cn.bakamc.folia.db.table.playerInfos
import cn.bakamc.folia.db.table.specialItems
import kotlinx.coroutines.*
import org.ktorm.dsl.limit
import org.ktorm.dsl.map
import org.ktorm.entity.filterColumns
import org.ktorm.entity.map
import org.ktorm.entity.toList
import java.nio.file.Path

fun main() {
    CoroutineScope(Dispatchers.Default).launch {
        println(Thread.currentThread().name)
        val i = suspendFun()
        println("先走一会")
        println(i.await())
    }
    runBlocking {
        delay(3000)
    }
}

fun CoroutineScope.suspendFun(): Deferred<Int> {
    return async {
        delay(2000)
        println(Thread.currentThread().name)
        114514
    }
}

suspend fun suspendFun2() {
    database {
        playerInfos.toList()
    }
}


val DefaultScope = CoroutineScope(Dispatchers.Default)

fun db() {
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


