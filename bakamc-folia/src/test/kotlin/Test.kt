
import cn.bakamc.folia.config.Configs
import cn.bakamc.folia.db.entity.PlayerVO
import cn.bakamc.folia.db.mapper.PlayerMapper
import cn.bakamc.folia.util.mapper
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import java.nio.file.Path

fun main() {
    db()
}


fun db() {
    Configs.init(Path.of("./build/config"))

    mapper<PlayerMapper>{
        selectPage(Page<PlayerVO>().setSize(2).setCurrent(1), QueryWrapper()).apply {
            records.forEach { println(it) }
        }
    }

    mapper<PlayerMapper>{
        update(PlayerVO("f01a8626-ba14-43ce-9fc0-67f9448c2771", "Elysia_same"), UpdateWrapper<PlayerVO>().eq("uuid","f01a8626-ba14-43ce-9fc0-67f9448c2771"))
    }


}