import org.junit.Test
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/8
 * 微信: yin80871901
 */


class Test3 {

    val queue = ArrayBlockingQueue<String>(100, true)

    val dcl = CountDownLatch(4)

    val ai = AtomicInteger()

    val producer1 = Runnable {
        for (i in 0..300) {
            queue.put("data1 : " + ai.incrementAndGet() )
        }
        dcl.countDown()
    }

    val producer2 = Runnable {
        for (i in 0..300) {
            queue.put("data2 : " + ai.incrementAndGet() )
        }
        dcl.countDown()
    }

    val producer3 = Runnable {
        for (i in 0..300) {
            queue.put("data3 : " + ai.incrementAndGet() )
        }
        dcl.countDown()
    }

    val producer4 = Runnable {
        for (i in 0..300) {
            if (i == 100) {
                throw RuntimeException("生产发生异常")
            }
        }
    }

    val productEs = Executors.newCachedThreadPool()


    val consumer1 = Runnable {
        while (true) {
            println(queue.take())
            Thread.sleep(100)
        }
    }

    val consumer2 = Runnable {
        while (true) {
            println(queue.take())
            Thread.sleep(100)
        }
    }

    val consumer3 = Runnable {
        while (true) {
            println(queue.take())
            if ( Math.random() * 100 < 100 ) {
                throw RuntimeException("消费者发生异常")
            }
        }
    }

    val cousumeEs = Executors.newFixedThreadPool(10)

    @Test
    fun test2() {

        productEs.submit(producer1)
        productEs.submit(producer2)
        productEs.submit(producer3)
        productEs.submit(producer4)

        cousumeEs.submit(consumer1)
        cousumeEs.submit(consumer2)
        cousumeEs.submit(consumer3)

        dcl.await()

        productEs.shutdown()
        cousumeEs.shutdown()

    }

}