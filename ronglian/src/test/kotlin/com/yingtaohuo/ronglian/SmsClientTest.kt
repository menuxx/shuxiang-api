package com.yingtaohuo.ronglian

import org.junit.Test

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/10/18
 * 微信: yin80871901
 */

class SmsClientTest {

    val CAPTCHA_ID = "198719"

    val appId = "8a216da85da6adf7015de9addca8199f"
    val accountSid = "8a216da857511049015774861894153f"
    val authToken = "e1de26cde6e24dd28dfedcd81467d1f0"

    @Test
    fun sendSmsTest() {
        val client = SmsClient(appId, accountSid, authToken)
        val json = client.sendSms("15158898469", CAPTCHA_ID, arrayOf("4678"))
        println(json.getString("statusCode") == "000000")
    }

}