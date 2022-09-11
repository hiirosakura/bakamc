import cn.bakamc.riguru.RiguruApplication
import cn.bakamc.riguru.services.TownServices
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

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
	fun test(){
		townServices.getAll().forEach{
			println(it.value)
		}
	}
}