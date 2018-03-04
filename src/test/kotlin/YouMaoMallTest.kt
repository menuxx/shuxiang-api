import com.fasterxml.jackson.databind.ObjectMapper
import com.menuxx.Application
import com.menuxx.mall.prop.YouhaoProps
import com.youhaosuda.Yhsd
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/23
 * 微信: yin80871901
 */

@SpringBootTest(classes = [Application::class])
@RunWith(SpringRunner::class)
class YouMaoMallTest {

    private final val logger = LoggerFactory.getLogger(YouMaoMallTest::class.java)

    @Autowired
    lateinit var objectMapper : ObjectMapper

     @Autowired
     lateinit var yhProp: YouhaoProps

    @Test
    fun authTest() {
        val auth = Yhsd.getInstance().auth(yhProp.appKey, yhProp.appSecret, "")
        val tokenData = objectMapper.readValue(auth.token.body, Map::class.java)
        logger.info(tokenData["token"] as String)
    }

}