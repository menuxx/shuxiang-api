import com.menuxx.apiserver.auth.AuthUserTypeNormal
import com.menuxx.apiserver.auth.TokenProcessor
import org.junit.Test

class AuthTest {

    @Test
    fun generateTokenTest() {
        val tokenProcessor = TokenProcessor("qidj983f8u2dh2", 7200)
        println(tokenProcessor.genToken("oRYmD1IPlrWLOJrmLX1dPca4tQcM", AuthUserTypeNormal, "192.168.0.1"))
    }

}