import cn.bakamc.common.common.PlayerInfo
import cn.bakamc.common.town.Town
import cn.bakamc.common.town.TownApplication
import cn.bakamc.riguru.RiguruApplication
import cn.bakamc.riguru.services.TownServices
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

/**
 *

 * 项目名 bakamc

 * 包名

 * 文件名 ServicesTest

 * 创建时间 2022/9/12 2:50

 * @author forpleuvoir

 */
@SpringBootTest(classes = [RiguruApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServicesTest {

	@Autowired
	lateinit var townServices: TownServices

	@Test
	fun test() {
		townServices.getAll().forEach {
			println(it.value)
		}
	}

	@Test
	fun join() {
		townServices.application(
			TownApplication(
				0,
				Town(
					1,
					"",
					"",
					Date(),
					ConcurrentLinkedDeque(),
					ConcurrentLinkedDeque(),
					ConcurrentLinkedDeque()
				),
				PlayerInfo(
					UUID.fromString("df51ea0f-cb93-4eba-b9ae-cb552cdf02e0"),
					"sheep056",
					"sheep056"
				),
				"我要加入小镇",
				Date()
			)
		)
	}
}