import com.menuxx.Application
import com.menuxx.miaosha.bean.UserObtainItemState
import com.menuxx.miaosha.queue.ChannelUserStateWriteQueue
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@SpringBootTest(classes = [Application::class])
@RunWith(SpringRunner::class)
@ActiveProfiles("development")
class ChannelUserStateWriteQueueTest {

    @Autowired
    lateinit var channelUserStateWriteQueue : ChannelUserStateWriteQueue

    @Test
    fun commitObtainStateTest() {

        (0..1000).map { i ->
            val uuid = UUID.randomUUID().toString()
            channelUserStateWriteQueue.commitObtainState(UserObtainItemState(
                    loopRefId = "${i}:" + uuid, userId = 2, channelItemId = 25, confirmState = 1, orderId = null, queueNum = null
            ))
            Thread.sleep(10)
            channelUserStateWriteQueue.commitConsumeState(UserObtainItemState(
                    loopRefId = "${i}:" + uuid, userId = 2, channelItemId = 25, confirmState = 3, orderId = null, queueNum = null
            ))
        }

        //channelUserStateWriteQueue.commitConsumeState(UserObtainItemState())
        //channelUserStateWriteQueue.commitConsumeState(UserObtainItemState())
        //channelUserStateWriteQueue.commitConsumeState(UserObtainItemState())
        //channelUserStateWriteQueue.commitConsumeState(UserObtainItemState())
        //channelUserStateWriteQueue.commitConsumeState(UserObtainItemState())
    }

}