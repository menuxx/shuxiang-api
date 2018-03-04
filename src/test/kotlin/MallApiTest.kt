import com.menuxx.mall.CookieJarStore
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Test
import java.util.concurrent.TimeUnit
import java.security.GeneralSecurityException
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager


/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/30
 * 微信: yin80871901
 */


class MallApiTest {

    @Test
    fun test1() {

        val trustManager = object : X509TrustManager {
            override fun checkClientTrusted(p0: Array<out java.security.cert.X509Certificate>?, p1: String?) {
            }

            override fun checkServerTrusted(p0: Array<out java.security.cert.X509Certificate>?, p1: String?) {
            }

            override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate>? {
                return emptyArray()
            }
        }

        try {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf<X509TrustManager>(trustManager), null)
            val sslSocketFactory = sslContext.socketFactory

            val DO_NOT_VERIFY = HostnameVerifier { _, _ -> true }

            val httpClient = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor(::println).setLevel(HttpLoggingInterceptor.Level.BODY))
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .pingInterval(10, TimeUnit.SECONDS)
                    .cookieJar(CookieJarStore())
                    .hostnameVerifier(DO_NOT_VERIFY)
                    .sslSocketFactory(sslSocketFactory)
                    .build()

            val req = Request.Builder().get().url("https://mall.nizhuantech.com/api/v1").build()

            val resp = httpClient.newCall(req).execute()
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        }


    }

}