package com.yingtaohuo.ronglian

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import org.slf4j.LoggerFactory
import yingtaohuo.HexUtil.bytesToHexString
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/10/18
 * 微信: yin80871901
 *
 * const accountSid = "8a216da857511049015774861894153f"
 * const authToken = "e1de26cde6e24dd28dfedcd81467d1f0"
 * const appId = "8a216da85da6adf7015de9addca8199f"
 */
class SmsClient(private val appId: String, private val accountSid: String, private val authToken: String) {

    private val logger = LoggerFactory.getLogger(SmsClient::class.java)

    private val httpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor { msg -> logger.debug("RongLian: " + msg) }.setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(4, TimeUnit.SECONDS)
            .build()

    private val Tpl = """
<?xml version="1.0" encoding="utf-8"?>
<TemplateSMS>
  <to>{toPhone}</to>
  <appId>{appId}</appId>
  <templateId>{templateId}</templateId>
  <datas>{datas}
  </datas>
</TemplateSMS>
"""

    private val format = SimpleDateFormat("yyyyMMddHHmmss")

    private val ApiUrl = "https://app.cloopen.com:8883/2013-12-26/Accounts/{accountSid}/SMS/TemplateSMS?sig={SigParameter}"


    private fun getTimestamp() : String {
        return format.format(Date())
    }


    private fun md5Hex(str: String) : String {
        val sha1 = MessageDigest.getInstance("MD5")
        return bytesToHexString(sha1.digest(str.toByteArray()))
    }

    private fun sign(timestamp: String) : String {
        return md5Hex(accountSid + authToken + timestamp).toUpperCase()
    }

    @Throws(SmsException::class)
    fun sendSms(toPhone:String, templateId: String, datas: Array<String>) : JSONObject {
        val xmlText = Tpl.replace("{toPhone}", toPhone, true)
                .replace("{appId}", appId, true)
                .replace("{templateId}", templateId, true)
                .replace("{datas}", datas.map { data -> "<data>$data</data>" }.joinToString("\n"))

        val xmlType = MediaType.parse("application/xml;charset=utf-8")

        val body = RequestBody.create(xmlType, xmlText)

        val timestamp = getTimestamp()

        val sign = sign(timestamp)

        val authHeader = Base64.getEncoder().encodeToString("$accountSid:$timestamp".toByteArray())

        val request = Request.Builder()
                .post(body)
                .header("Accept", "application/json")
                .header("Authorization", authHeader)
                .url(
                ApiUrl
                        .replace("{accountSid}", accountSid)
                        .replace("{SigParameter}", sign)

        ).build()

        val resp = httpClient.newCall(request).execute()

        if ( resp.isSuccessful ) {
            val body = resp.body()
            if ( body != null ) {
                val data = JSONObject(body.string())
                if ( data.getString("statusCode") == "000000" ) {
                    return data
                } else {
                    throw SmsException("statusMsg: ${data.getString("statusMsg")}, statusCode: ${data.getString("statusCode")}")
                }
            } else {
                throw SmsException("返回的消息为空")
            }
        }
        throw SmsException("短信网关调用异常")
    }

}