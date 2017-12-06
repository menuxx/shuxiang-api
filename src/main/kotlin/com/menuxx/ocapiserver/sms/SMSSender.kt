package com.menuxx.ocapiserver.sms

import com.menuxx.ocapiserver.prop.RongLianProps
import com.yingtaohuo.ronglian.SmsClient
import com.yingtaohuo.ronglian.SmsException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/5
 * 微信: yin80871901
 */

@Service
class SMSSender(private val rongLianProps: RongLianProps) {

    private val smsClient = SmsClient(rongLianProps.appId, rongLianProps.accountSid, rongLianProps.authToken)

    private val logger = LoggerFactory.getLogger(SMSSender::class.java)

    private fun sendSmsByTplId(mobile: String, tplId: String, data: Array<String>) : Boolean {
        return try {
            val json = smsClient.sendSms(mobile, tplId, data)
            logger.info("tplId: $tplId, dateCreated: ${json.getString("dateCreated")}, smsMessageSid: ${json.getString("smsMessageSid")}")
            true
        } catch (ex: SmsException) {
            logger.error("tplId: $tplId, errorMsg: ${ex.message}")
            false
        }
    }

    fun sendCaptcha(mobile: String, data: Array<String>) = sendSmsByTplId(mobile, rongLianProps.smsTpls.captchaId, data)

    fun sendOrderSuccess(mobile: String, data: Array<String>) = sendSmsByTplId(mobile, rongLianProps.smsTpls.buySuccessId, data)

}