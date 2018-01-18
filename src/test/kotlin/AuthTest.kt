import com.menuxx.code.parseUrlPathCode
import com.menuxx.sso.auth.AuthUserTypeNormal
import com.menuxx.sso.auth.TokenProcessor
import org.junit.Test

class AuthTest {

    @Test
    fun generateTokenTest() {
        val tokenProcessor = TokenProcessor("qidj983f8u2dh2", 7200)
        println(tokenProcessor.genToken("oRYmD1IPlrWLOJrmLX1dPca4tQcM", AuthUserTypeNormal, "192.168.0.1"))
    }

    @Test
    fun urlTest() {
        println(parseUrlPathCode("http://t.nizhuantech.com/tc/~23123~sadasd213"))
    }

}