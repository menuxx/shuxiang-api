import com.lmax.disruptor.*
import com.lmax.disruptor.dsl.Disruptor
import com.lmax.disruptor.dsl.ProducerType
import org.junit.Test
import java.nio.ByteBuffer
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/7
 * 微信: yin80871901
 */

data class ChannelItem(
        val channelId: Int,
        val itemId: Int
)

data class ChannelUser(
        var channelId: Int?,
        var userId: Int?,
        var confirmAck: Int?
)

class ChannelUserThreadFactory : ThreadFactory {
    override fun newThread(runnable: Runnable): Thread {
        return Thread(runnable, "Channel-User-thread(" + UUID.randomUUID().toString() + ")")
    }
}

class WriteToDbEventHandler : EventHandler<ChannelUser> {
    override fun onEvent(user: ChannelUser, sequence: Long, endOfBatch: Boolean) {
        if (user.confirmAck == 1) {
            println(user)
            Thread.sleep(50)
        }
    }
}

class ChannelUserEventHandler : EventHandler<ChannelUser> {

    data class ChannelItem(
            val id: Int,
            val channelId: Int,
            val itemId: Int,
            val itemName: String,
            var obtainTime: Instant?,
            var obtainUserId: Int?
    )

    fun fetchObtain(userId: Int, channel: ConcurrentHashMap<Int, ChannelItem>) : ChannelItem? {
        return channel.search((channel.size / 100).toLong(), { k, v ->
            if (v.obtainUserId == userId && Duration.between(v.obtainTime, Instant.now()).seconds < 30) {
                // 如果查找到 会导致删除
                channel.remove(k)
                v
            } else {
                null
            }
        })
    }

    // 同时只能有一个线程完成
    fun obtainChannelItem(userId: Int, channel: ConcurrentHashMap<Int, ChannelItem>) : ChannelItem? {
        val freeItems = channel.filter { entry ->
            val obtainTime = entry.value.obtainTime ?: Instant.now()
            entry.value.obtainUserId == null || Duration.between(obtainTime, Instant.now()).seconds > 30
        }
        if ( freeItems.isEmpty() ) {
            return null
        }
        val obtainKey = freeItems.keys.first()
        val obtain = freeItems[obtainKey]!!
        // g更新持有人和持有时间
        obtain.obtainUserId = userId
        obtain.obtainTime = Instant.now()
        return obtain
    }


    companion object {

        private val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .withLocale(Locale.CHINESE)
                .withZone(ZoneId.systemDefault())

        private val channel1 = ConcurrentHashMap<Int, ChannelItem>()

        private val channel2 = ConcurrentHashMap<Int, ChannelItem>()

        private val channel3 = ConcurrentHashMap<Int, ChannelItem>()

        private lateinit var channelStore: HashMap<Int, ConcurrentHashMap<Int, ChannelItem>>

        private val counters = arrayOf(AtomicInteger(1), AtomicInteger(1), AtomicInteger(1))

        private val usersState = ConcurrentHashMap<Int, ChannelUser>()

        fun setup() : List<ChannelUser> {

            for ( i in 1..201 ) {
                channel1.put(i, ChannelItem(i + 1, 1, i + 2, "由来久矣", null, null))
            }

            for ( i in 202..402 ) {
                channel2.put(i, ChannelItem(i + 1, 2, i + 2, "球状闪电", null, null))
            }

            for ( i in 404..604 ) {
                channel3.put(i, ChannelItem(i + 1, 3, i + 2, "从零到一", null, null))
            }

            channelStore = hashMapOf(
                    1 to channel1,
                    2 to channel2,
                    3 to channel3
            )

            val users = (1..2001).map { i ->
                ChannelUser(
                        userId = i,
                        channelId = Math.floor( Math.random() + 1.5 ).toInt() + Math.floor( Math.random() + 0.5 ).toInt(), // 1 or 2 or 3
                        confirmAck = null
                )
            }

            println(" channelId 1 user count: " + users.filter { it.channelId == 1 }.size + " book count : " + channel1.size)

            println(" channelId 2 user count: " + users.filter { it.channelId == 2 }.size + " book count : " + channel2.size)

            println(" channelId 3 user count: " + users.filter { it.channelId == 3 }.size + " book count : " + channel3.size)

            return users

        }

    }

    override fun onEvent(currentUser: ChannelUser, sequence: Long, endOfBatch: Boolean) {

        try {

            val channelId = currentUser.channelId!!
            val channel = channelStore[currentUser.channelId!!]!!
            val counter = counters[currentUser.channelId!! - 1]
            val userId = currentUser.userId!!

            var sessionUser = usersState[userId]

            if (sessionUser == null) {
                sessionUser = currentUser.copy()
                usersState.put(userId, sessionUser)
            }

            // 等于 null 就是抢名额
            if ( sessionUser.confirmAck == null ) {
                val channelItem = obtainChannelItem(userId, channel)
                println("obtainChannelItem start")
                // 标记状态
                if (channelItem != null) {
                    currentUser.confirmAck = 0
                    sessionUser.confirmAck = 0
                    println("obtainChannelItem ok")
                } else {
                    if ( channel.size == 0 ) {
                        println("抢完了，不要伤心，下次要来早一点，一定能抢到")
                    } else {
                        println("刷一刷，还有机会能抢到")
                    }
                }
            }
            // 等于 0 就是支付确认
            // 需要生成 序号，订单号
            else if ( sessionUser.confirmAck == 0 ) {
                // println("genorder")
                // 原子计数器
                // 生成序号
                val queueNum = counter.getAndIncrement()
                // 生成订单编号
                val orderNo = formatter.format(Instant.now()) + userId + channelId + queueNum
                // 获取该用户之前抢到过的 item
                val channelItem = fetchObtain(userId, channel)
                if ( channelItem == null ) {
                    println("用户编号: $userId ，太长时间没有确认，已经被别人领走了")
                } else {
                    currentUser.confirmAck = 1
                    sessionUser.confirmAck = 1
                    println("用户编号: $userId 订单编号: $orderNo, 序号: $queueNum, 渠道编号: $channelId, 书籍名称: ${channelItem.itemName}")
                }
            } else if ( sessionUser.confirmAck == 1 ) {
                println("一个人只能抢一本")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

class ChannelUserEventProducer(private val ringBuffer: RingBuffer<ChannelUser>) {
    private val translator = ChannelUserEventTranslator()
    fun product(bb: ByteBuffer) {
        ringBuffer.publishEvent<ByteBuffer>(translator, bb)
    }
}

class ChannelUserEventTranslator : EventTranslatorOneArg<ChannelUser, ByteBuffer> {
    override fun translateTo(event: ChannelUser, sequence: Long, byteBuffer: ByteBuffer) {
        event.userId = byteBuffer.int
        event.channelId = byteBuffer.int
    }
}

class MyExceptionHandler : ExceptionHandler<ChannelUser> {
    override fun handleOnShutdownException(ex: Throwable) {
        ex.printStackTrace()
    }

    override fun handleEventException(ex: Throwable, sequence: Long, event: ChannelUser) {
        ex.printStackTrace()
    }

    override fun handleOnStartException(ex: Throwable) {
        ex.printStackTrace()
    }
}

class ChannelUserFactory : EventFactory<ChannelUser> {
    override fun newInstance(): ChannelUser {
        return ChannelUser(null, null, null)
    }
}

class Test2 {

    @Test
    fun test1() {

        val ringBufferSize = 65536

        val eventFactory = ChannelUserFactory()

        val threadFactory = ChannelUserThreadFactory()

        val disruptor = Disruptor<ChannelUser>(eventFactory, ringBufferSize, threadFactory, ProducerType.MULTI, YieldingWaitStrategy())

        disruptor.setDefaultExceptionHandler(MyExceptionHandler())

        disruptor.handleEventsWith(ChannelUserEventHandler()).then(WriteToDbEventHandler())

        disruptor.start()

        val producer = ChannelUserEventProducer(disruptor.ringBuffer)

        val es1 = Executors.newFixedThreadPool(100)

        val es2 = Executors.newCachedThreadPool()

        val users = ChannelUserEventHandler.setup()

        val cdl = CountDownLatch(10000)

        val startTime = System.currentTimeMillis()

        (1..10000).map {
            val future = es2.submit {
                val userNum = Math.floor( Math.random() * 2001 ).toInt()
                val currentUser = users[userNum]
                val bb = ByteBuffer.allocate(512)
                bb.putInt(currentUser.userId!!)
                bb.putInt(currentUser.channelId!!)
                bb.flip()
                producer.product(bb)
                cdl.countDown()
            }
        }

        cdl.await()

        es2.shutdown()
        disruptor.shutdown()

        val endTime = System.currentTimeMillis()

        println("total time - " + (endTime - startTime) )

    }

}