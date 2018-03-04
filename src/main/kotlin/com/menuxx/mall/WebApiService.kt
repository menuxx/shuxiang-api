package com.menuxx.mall

import com.fasterxml.jackson.databind.ObjectMapper
import com.menuxx.mall.exception.YouHaoException
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.lang3.StringUtils
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.message.BasicNameValuePair
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.Charset
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/23
 * 微信: yin80871901
 */

// 忽略 ssl 证书不正常
fun OkHttpClient.Builder.ignoreSSlConnectionSecure() : OkHttpClient.Builder {
    val trustManager = object : X509TrustManager {
        override fun checkClientTrusted(p0: Array<out java.security.cert.X509Certificate>?, p1: String?) {}
        override fun checkServerTrusted(p0: Array<out java.security.cert.X509Certificate>?, p1: String?) {}
        override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate>? { return emptyArray() }
    }
    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, arrayOf<X509TrustManager>(trustManager), null)
    val sslSocketFactory = sslContext.socketFactory
    val notVerify = HostnameVerifier { _, _ -> true }
    this.hostnameVerifier(notVerify).sslSocketFactory(sslSocketFactory)
    return this
}

@Service
class WebApiService(
        private val objectMapper: ObjectMapper
) {

    private final val AccessToken = "X-API-ACCESS-TOKEN"

    private final val logger = LoggerFactory.getLogger(WebApiService::class.java)

    private final val httpClient: OkHttpClient

    private final val apiBaseUrl = "https://mall.nizhuantech.com/api/v1/"

    init {
        httpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor(logger::debug).setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .pingInterval(10, TimeUnit.SECONDS)
                .cookieJar(CookieJarStore())
                .ignoreSSlConnectionSecure()
                .build()
    }

    private fun makeException(resp: Response) : YouHaoException {
        return if ( resp.body() == null ) {
            logger.warn("http 异常 response body 为空")
            YouHaoException(resp.code(), resp.message(), null)
        } else {
            YouHaoException(resp.code(), resp.message(), objectMapper.readValue(resp.body()!!.bytes(), HashMap::class.java) as HashMap<String, Any>)
        }
    }

    private fun makeException(respBody: HashMap<String, Any>) : YouHaoException {
        val code = respBody["code"] as Int
        val message = respBody["message"] as String
        return YouHaoException(code, message, respBody)
    }

    private fun paramToQs(params: Map<String, Any>) : String {
        return URLEncodedUtils.format(params.entries.map { BasicNameValuePair(it.key, StringUtils.defaultString("${it.value}", "")) }, Charset.forName("UTF-8"))
    }

    /**
     * 需要认证的接口，通过该方法调用
     */
    @Throws(IOException::class, YouHaoException::class)
    fun authRequest(method: String, path: String, customerSession: String, accessToken: String, queryData: HashMap<String, String>? = null, data: HashMap<String, Any>? = null) : Map<String, Any?> {
        val _queryData = if ( queryData == null ) {
            hashMapOf("rnd" to System.currentTimeMillis().toString())
        } else {
            queryData["rnd"] = System.currentTimeMillis().toString()
            queryData
        }
        val reqBody = if ( method == "POST" || method == "PUT" ) {
            if ( data == null ) {
                throw YouHaoException(400, "请求参数不能为空", null)
            }
            val form = MediaType.parse(URLEncodedUtils.CONTENT_TYPE)
            // 将 map 转换成 符合 x-www-form-urlencoded 的 querystring
            data["token"] = accessToken
            // 只有当 method 是 post 或 put 的时候，才会有 body
            RequestBody.create(form, paramToQs(data))
        } else null

        val reqPath = if ( method == "GET" || method == "DELETE" ) {
            // 有参数就拼接参数
            val qs = if (queryData != null) "?" + paramToQs(_queryData) else ""
            "$path$qs"
        } else { path }

        val sessionCookie = Cookie.Builder()
                .domain(URI.create(apiBaseUrl).host)
                .expiresAt(Instant.now().plusSeconds(3600).epochSecond)
                .httpOnly()
                .name("_homeland_shop_customer_session")
                .value(customerSession)
                .secure()
                .build()

        val request = Request.Builder()
                .method(method, reqBody)
                .header(AccessToken, accessToken)
                .header("Cookie", sessionCookie.toString())
                .url(apiBaseUrl + reqPath)
                .build()

        val call = httpClient.newCall(request)
        val resp = call.execute()
        if (resp.code() == 200) {
            val body = resp.body()
            if ( body != null ) {
                val respBody = objectMapper.readValue(body.byteStream(), HashMap::class.java) as HashMap<String, Any>
                val code = respBody["code"] as Int
                if ( code == 200 ) {
                    return respBody
                } else {
                    throw makeException(respBody)
                }
            } else {
                logger.warn("http 正常返回但是 response body 为空")
            }
        }
        throw makeException(resp)
    }

    /**
     * 密码错误:
     * {"code":201,"message":"账号和密码不匹配"}
     * 正常返回：
     * {"code":200,"message":"","token":"ee4ccf800e863791b11f9eeedba76e9b18cc436fe3a89a5dafe59eb885552319","account":"for12345@example.com","customer":{"accept_marketing":true,"avatar_url":"//asset.ibanquan.com/image/569547d30abc3e71be000007/customer_default.png","birthday":null,"customer_level":{"avatar_url":"//asset.ibanquan.com/image/569547ca0abc3e71be000003/custom_level_default.png","credits":0,"discount":100,"id":13307,"name":"普通会员"},"id":954553,"indentity_card":"3622**********2933","last_order_at":null,"last_order_no":null,"metas":{},"name":"for12345","notify_email":"for@example.com","notify_phone":"13632269380","orders_count":0,"real_name":"张三","regist_at":"2018-01-23T20:58:53.327+08:00","sex":"undefined","social_accounts":[{"avatar_url":"","binded":false,"bind_url":"https://youhaosuda.com/api/auth?type=douban&direct_bind=true","name":"","type":"douban"},{"avatar_url":"","binded":false,"bind_url":"https://youhaosuda.com/api/auth?type=weibo&direct_bind=true","name":"","type":"weibo"},{"avatar_url":"","binded":false,"bind_url":"https://youhaosuda.com/api/auth?type=qq&direct_bind=true","name":"","type":"qq"},{"avatar_url":"","binded":false,"bind_url":"https://youhaosuda.com/api/auth?type=renren&direct_bind=true","name":"","type":"renren"},{"avatar_url":"","binded":false,"bind_url":"https://youhaosuda.com/api/auth?type=netease&direct_bind=true","name":"","type":"netease"},{"avatar_url":"","binded":false,"bind_url":"https://youhaosuda.com/api/auth?type=weixin&direct_bind=true","name":"","type":"weixin"},{"avatar_url":"","binded":false,"bind_url":"https://youhaosuda.com/api/auth?type=facebook&direct_bind=true","name":"","type":"facebook"}],"social_type":false,"total_spent":0,"total_credit":0,"point":0,"last_year_point":0,"uname":null,"email":"for12345@example.com","mobile":null,"social_id":null,"reg_type":1}}
     */
    @Throws(IOException::class, YouHaoException::class)
    fun accountLogin(account: String, password: String) : HashMap<String, Any> {
        val formData = MediaType.parse("application/x-www-form-urlencoded")
        val _account = URLEncoder.encode(account, "UTF-8")
        val reqBody = RequestBody.create(formData, "account=$_account&password=$password")
        val request = Request.Builder()
                .post(reqBody)
                .url(apiBaseUrl + "account/login")
                .build()
        val call = httpClient.newCall(request)
        val resp = call.execute()
        if (resp.code() == 200) {
            val body = resp.body()
            if ( body != null ) {
                val respBody = objectMapper.readValue(body.byteStream(), HashMap::class.java) as HashMap<String, Any>
                val code = respBody["code"] as Int
                if ( code == 200 ) {
                    return respBody
                } else {
                    throw makeException(respBody)
                }
            } else {
                logger.warn("http 正常返回但是 response body 为空")
            }
        }
        throw makeException(resp)
    }

}

enum class YouHaoWebApi(val path: String, val method: String) {
    CartCreate("cart/create", "POST"), // 购物侧添加sku商品
    AddressCreate("address/create", "POST"), // 收货地址创建
    Address("address", "GET"), // 获取地址
    WithinShipments("cart/within_shipments", "GET"), // 获取支付方式和物流方式
    PaymentMethod("payment_method", "GET"), // 支持的支付方式
    OrderCreate("order/create", "POST"), // 订单创建
    Cart("cart", "GET"), // 购物车详情
    CartSet("cart/set", "POST"), // 购物车商品数量修改
    CartDiscount("discount/match/cart", "GET") // 购物车折扣
}