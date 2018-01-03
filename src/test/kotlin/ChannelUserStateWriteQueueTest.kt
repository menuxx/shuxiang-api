import com.menuxx.Application
import com.menuxx.miaosha.bean.UserObtainItemState
import com.menuxx.miaosha.queue.ChannelUserStateWriteQueue
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest(classes = [Application::class])
@RunWith(SpringRunner::class)
@ActiveProfiles("development")
class ChannelUserStateWriteQueueTest {

    @Autowired
    lateinit var channelUserStateWriteQueue : ChannelUserStateWriteQueue

    @Test
    fun commitObtainStateTest() {
        (0..10).map {
            channelUserStateWriteQueue.commitObtainState(UserObtainItemState(
                    loopRefId = "25:61247126-412348-2341234213-42342314-324132", userId = 2, channelItemId = 25, confirmState = 2, orderId = null, queueNum = null
            ))
        }
        //channelUserStateWriteQueue.commitConsumeState(UserObtainItemState())
        //channelUserStateWriteQueue.commitConsumeState(UserObtainItemState())
        //channelUserStateWriteQueue.commitConsumeState(UserObtainItemState())
        //channelUserStateWriteQueue.commitConsumeState(UserObtainItemState())
        //channelUserStateWriteQueue.commitConsumeState(UserObtainItemState())
    }

}