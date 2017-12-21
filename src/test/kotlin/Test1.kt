import org.junit.BeforeClass
import org.junit.Test
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.time.ZoneId
import java.util.Locale
import java.time.format.DateTimeFormatter



/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/7
 * 微信: yin80871901
 */

class Test1 {

    // @Test
    fun test() {

        val map = ConcurrentHashMap<Int, String>()

        for (i in 1..10000) {
            map.put(i, "$i a string")
        }

        val cdl = CountDownLatch(3)

        Thread {
            val startTime = System.currentTimeMillis()
            for (e in map) {
                e.setValue("${e.value} aaaaaaaaaaaa")
            }
            val endTime = System.currentTimeMillis()
            println("1 - " + (endTime - startTime) )
            cdl.countDown()
        }.start()

        Thread {
            val startTime = System.currentTimeMillis()
            for (e in map) {
                e.setValue("${e.value} bbbbbbbbbbbb")
            }
            val endTime = System.currentTimeMillis()
            println("2 - " + (endTime - startTime) )
            cdl.countDown()
        }.start()

        Thread {
            val startTime = System.currentTimeMillis()
            for (e in map) {
                e.value + "222222"
            }
            val endTime = System.currentTimeMillis()
            println("3 - " + (endTime - startTime) )
            cdl.countDown()
        }.start()

        cdl.await()

    }

    data class ChannelItem(
            val id: Int,
            val channelId: Int,
            val itemId: Int,
            val itemName: String,
            var obtainTime: Instant?,
            var obtainUserId: Int?
    )


    companion object {

        private val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .withLocale(Locale.CHINESE)
                .withZone(ZoneId.systemDefault())

        private val channel1 = ConcurrentHashMap<Int, ChannelItem>(100)

        private val channel2 = ConcurrentHashMap<Int, ChannelItem>(200)

        private val channel3 = ConcurrentHashMap<Int, ChannelItem>(500)

        private val users = ConcurrentHashMap<Int, ChannelItem>()

        private lateinit var channelStore: HashMap<Int, ConcurrentHashMap<Int, ChannelItem>>

        @JvmStatic
        @BeforeClass
        fun setup() {

            for ( i in 1..100 ) {
                channel1.put(i, ChannelItem(1, i + 2, i, "由来久矣", null, null))
            }

            for ( i in 1..200 ) {
                channel2.put(i, ChannelItem(1, i + 2, i, "球状闪电", null, null))
            }

            for ( i in 1..500 ) {
                channel3.put(i, ChannelItem(1, i + 2, i, "从零到一", null, null))
            }

            channelStore = hashMapOf(
                    1 to channel1,
                    2 to channel2,
                    3 to channel3
            )

        }

    }

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

    @Test
    fun test2() {

        val counters = arrayOf(AtomicInteger(1), AtomicInteger(1), AtomicInteger(1))

        val users = (1..701).map { i ->
            val map = ConcurrentHashMap<String, Int>()
            map["userId"] = i
            map["channelId"] = Math.floor( Math.random() + 1.5 ).toInt() + Math.floor( Math.random() + 0.5 ).toInt() // 1 or 2 or 3
            map
        }

        println(" channelId 1 user count: " + users.filter { it["channelId"] == 1 }.size + " book count : " + channel1.size)

        println(" channelId 2 user count: " + users.filter { it["channelId"] == 2 }.size + " book count : " + channel2.size)

        println(" channelId 3 user count: " + users.filter { it["channelId"] == 3 }.size + " book count : " + channel3.size)

        // 5 万个人同时抢

        val es = Executors.newCachedThreadPool { r ->
            Thread(r, "BookReader")
        }

        val cdl = CountDownLatch(10000)

        val startTime = System.currentTimeMillis()

        // 1 万次多线程并发
        (1..10001).map { _ ->
            // 每个人两次，第二次，就是付款，确认下单
            es.submit {
                cdl.countDown()
                try {
                    // 1- 3001
                    val userNum = Math.floor( Math.random() * 701 ).toInt()
                    val currentUser = users[userNum]
                    val userId = currentUser["userId"]!!
                    val channelId = currentUser["channelId"]!!
                    val channel = channelStore[channelId]!!
                    // 等于 null 就是抢名额
                    if ( currentUser["confirmAck"] == null ) {
                        val channelItem = obtainChannelItem(userId, channelStore[channelId]!!)
                        // println("obtainChannelItem start")
                        // 标记状态
                        if (channelItem != null) {
                            currentUser["confirmAck"] = 0
                            // println("obtainChannelItem ok")
                        } else {
                            if ( channel.size == 0 ) {
                                //println("抢完了，不要伤心，下次要来早一点，一定能抢到")
                            } else {
                                //println("刷一刷，还有机会能抢到")
                            }
                        }
                    }
                    // 等于 0 就是支付确认
                    // 需要生成 序号，订单号
                    else if ( currentUser["confirmAck"] == 0 ) {
                        // println("genorder")
                        // 原子计数器
                        val counter = counters[channelId - 1]
                        // 生成序号
                        val queueNum = counter.getAndIncrement()
                        // 生成订单编号
                        val orderNo = formatter.format(Instant.now()) + userId + channelId + queueNum
                        // 获取该用户之前抢到过的 item
                        val channelItem = fetchObtain(userId, channel)
                        if ( channelItem == null ) {
                            // println("用户编号: $userId ，太长时间没有确认，已经被别人领走了")
                        } else {
                            currentUser["confirmAck"] = 1
                            println("用户编号: $userId 订单编号: $orderNo, 序号: $queueNum, 渠道编号: $channelId, 书籍名称: ${channelItem.itemName}")
                        }
                    } else if ( currentUser["confirmAck"] == 1 ) {
                        // println("一个人只能抢一本")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        val endTime = System.currentTimeMillis()

        cdl.await()

        println("total time - " + (endTime - startTime) )

        es.shutdown()   // 会等待所有线程执行完毕才会 关闭

    }

}