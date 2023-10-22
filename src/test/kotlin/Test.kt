
import cn.bakamc.folia.config.Configs
import cn.bakamc.folia.db.database
import cn.bakamc.folia.db.table.PlayerInfo
import cn.bakamc.folia.db.table.specialItems
import org.ktorm.entity.map
import java.nio.file.Path

fun main() {
    db()
}


fun db() {
    Configs.init(Path.of("./build/config"))

    val info = PlayerInfo {
        uuid = "808e8bd1-f808-4dec-aa46-ad64d3d6dc1c"
        name = "forpleuvoir"
    }
    database {
        specialItems.map { it }
    }.forEach {
        println(it)
    }
}

val list = listOf(
    "Mikatsuki_R",
    "fenrage",
    "skost12",
    "WakaDoki",
    "LiaysE",
    "dycruence",
    "ling_xingyue1",
    "A_Qian555",
    "StarsNova",
    "Cigargar",
    "gaye",
    "wuxianxiaoshi",
    "Clearsky404",
    "Pandaman_SSK",
    "Xyaobye",
    "Morrison_Glazkov",
    "OTTO_officiall",
    "Umik09",
    "xmzh",
    "suikaaa",
    "BelovedsCici",
    "YUMOOOOOOOO",
    "LangZhengMing",
    "dhwuia"
)

val listUUID = listOf(
    "7909d24f-dfc1-4fde-b2ff-fea08dc67ffd",
    "793b8fcf-bb55-4543-b269-d2779c438813",
    "7988b94d-8570-4e19-8633-c63c6f142cb1",
    "79fa7850-7cbe-40e8-aac6-856fc45f9f8f",
    "79fbba47-5ed6-488b-b81d-af6a7b156653",
    "7a7efbf8-41ad-4b83-ba8a-ea44b497efba",
    "7b38b5b9-b069-4c70-9808-6449e2e50a4c",
    "7b5ca190-b2d9-458c-bf8d-23c0dab8bf6a",
    "7bc9d565-9b60-4159-8fb0-c2a69b4e0ea4",
    "7bd052b6-9bd6-447e-84a2-51db960bdae7",
    "7c6bc2c3-a75d-49b9-97e3-a1a5ac2959f2",
    "7c8ed013-46f9-4f2d-9f2e-0309421ee0b7",
    "7c9614a3-33e5-4d9b-8cf2-c5b581230d56",
    "7e02c6f8-0c78-4e9c-9e00-38748596f0e1",
    "7e5ba35f-2a70-49a4-9e14-f009316d9c06",
    "7e60190e-25c3-416b-a34c-df8bcbf98b48",
    "7f62c248-c611-4bfc-b821-8a47bcbc1441",
    "7f70f00b-f6e2-4401-aea8-66f3c261522d",
    "7f835697-e69b-47dd-8c23-e83a551aa122",
    "808e8bd1-f808-4dec-aa46-ad64d3d6dc1c",
    "82c29204-3846-4bc1-b826-eb6eb42df6f3",
    "83c91549-f588-4a81-a4f9-6fae72c8e24a",
    "83e2df1b-39f2-48bf-a095-9c25cec5ad9c",
    "83f92c7b-ecf9-4220-b417-8febe789cdc7"
)

fun test1() {
    "id && name && tag".split(" ").forEach {
        println(it)
    }
}