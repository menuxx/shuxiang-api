import org.junit.Test
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.*

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/10
 * 微信: yin80871901
 */

class Test5 {

    @Test
    fun test() {

        val buffer = ByteBuffer.allocate(512)

        buffer.put(1)
        buffer.put(2)

        val uuid = UUID.randomUUID().toString()

        println(uuid)

        val strBuf = ByteBuffer.wrap(uuid.toByteArray(Charset.forName("UTF-8")))

        buffer.put(strBuf)

        buffer.flip()

        println(buffer.get())
        println(buffer.get())

        buffer.position(2)

        val s = Charset.forName("UTF-8").decode(buffer).toString()

        println(s)

    }

}