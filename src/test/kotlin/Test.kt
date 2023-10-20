import cn.bakamc.folia.config.Configs
import cn.bakamc.folia.db.entity.FlightEnergyVO
import cn.bakamc.folia.db.entity.PlayerVO
import cn.bakamc.folia.db.initDataBase
import cn.bakamc.folia.db.mapper.FlightEnergyMapper
import cn.bakamc.folia.db.mapper.PlayerMapper
import cn.bakamc.folia.extension.uuid
import cn.bakamc.folia.service.PlayerService
import cn.bakamc.folia.util.mapper
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import java.nio.file.Path
import kotlin.random.Random

fun main() {
    db()
}


fun db() {
    Configs.init(Path.of("./build/config"))
    initDataBase()
    val list: List<PlayerVO>
    mapper<PlayerMapper> {
        list = selectList(QueryWrapper())
    }
    println("获取到的玩家数:${list.size}")
    list.forEach {
        println(it)
    }
//    mapper<FlightEnergyMapper> {
//        list.forEach { player -> insert(FlightEnergyVO(player.uuid, String.format("%.2f", Random.nextDouble(5000.0)).toDouble())) }
//    }
//    mapper<PlayerMapper>{
//        selectPage(Page<PlayerVO>().setSize(2).setCurrent(1), QueryWrapper()).apply {
//            records.forEach { println(it) }
//        }
//    }
//
//    mapper<PlayerMapper>{
//        update(PlayerVO("f01a8626-ba14-43ce-9fc0-67f9448c2771", "Elysia_same"), UpdateWrapper<PlayerVO>().eq("uuid","f01a8626-ba14-43ce-9fc0-67f9448c2771"))
//    }


}

fun test1() {
    "id && name && tag".split(" ").forEach {
        println(it)
    }
}