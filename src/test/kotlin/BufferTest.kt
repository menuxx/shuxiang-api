import com.menuxx.genRandomString
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.charset.Charset

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/19
 * 微信: yin80871901
 */


class BufferTest {

    @Test
    fun test2() {
        (0..100).map {
            println(genRandomString(it))
        }
    }

    @Test
    fun test1() {
        val byteBuffer = ByteBuffer.allocate(512)
        byteBuffer.putInt(1)
        byteBuffer.putInt(2)
        byteBuffer.putInt(4)
        byteBuffer.putInt(6)
        byteBuffer.put(ByteBuffer.wrap("abcdefg0123,abcdefg0123,abcdefg0123,abcdefg0123,abcdefg0123,abcdefg0123,abcdefg0123,abcdefg0123".toByteArray(Charset.forName("UTF-8"))))
        // byteBuffer.flip()
        byteBuffer.flip()

        println(byteBuffer.getInt())
        println(byteBuffer.getInt())
        println(byteBuffer.getInt())
        println(byteBuffer.getInt())
        // byteBuffer.position(2)
        println(Charset.forName("UTF-8").decode(byteBuffer).toString())
    }

}