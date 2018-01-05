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

        (0..10000).map {
            Thread.sleep(10)
            channelUserStateWriteQueue.commitObtainState(UserObtainItemState(
                    loopRefId = "25:" + UUID.randomUUID().toString(), userId = 2, channelItemId = 25, confirmState = 2, orderId = null, queueNum = null
            ))
        }

        //channelUserStateWriteQueue.commitConsumeState(UserObtainItemState())
        //channelUserStateWriteQueue.commitConsumeState(UserObtainItemState())
        //channelUserStateWriteQueue.commitConsumeState(UserObtainItemState())
        //channelUserStateWriteQueue.commitConsumeState(UserObtainItemState())
        //channelUserStateWriteQueue.commitConsumeState(UserObtainItemState())
    }

}