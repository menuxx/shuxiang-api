import org.junit.BeforeClass
import org.junit.Test
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/7
 * 微信: yin80871901
 */

class Test1 {

    @Test
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
            val obtainTime: Instant?,
            val obtainUserId: Int?
    )

    companion object {

        private val channel1 = ConcurrentHashMap<Int, ChannelItem>(1000)

        private val channel2 = ConcurrentHashMap<Int, ChannelItem>(2000)

        private lateinit var channelStore: HashMap<Int, ConcurrentHashMap<Int, ChannelItem>>

        @BeforeClass
        fun setup() {

            for ( i in 1..1000 ) {
                channel1.put(i, ChannelItem(1, i + 2, i, "由来久矣", null, null))
            }

            for ( i in 1..2000 ) {
                channel2.put(i, ChannelItem(1, i + 2, i, "球状闪电", null, null))
            }

            channelStore = hashMapOf(
                    1 to channel1,
                    2 to channel2
            )

        }

    }

    @Synchronized
    fun obtainChannelItem(userId: Int, channel: ConcurrentHashMap<Int, ChannelItem>) : ChannelItem? {
        val freeItem = channel.filter { entry ->
            entry.value.obtainUserId != null && Duration.between(entry.value.obtainTime, Instant.now()).seconds < 30
        }
        return freeItem[0]
    }

    @Test
    fun test2() {


        val es = Executors.newCachedThreadPool { r ->
            Thread(r, "BookReader")
        }

        // 5 玩个人同时抢
        for (i in 1..50000) {
            es.submit {
                // 随机产生用户数据
                // 1 or 2
                val channelId = Math.floor( Math.random() + 1.5 ).toInt()
                val userId = Math.floor( Math.random() * 2000 ).toInt()
                obtainChannelItem(userId, channelStore[channelId]!!)
            }
        }

        es.shutdown()   // 会等待所有线程执行完毕才会 关闭

    }

}