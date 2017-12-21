package com.menuxx.common.sms

import com.menuxx.common.prop.RongLianProps
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

    @Throws(SmsException::class)
    private fun sendSmsByTplId(mobile: String, tplId: String, data: Array<String>) {
        return try {
            val json = smsClient.sendSms(mobile, tplId, data)
            logger.info("tplId: $tplId, dateCreated: ${json.getString("dateCreated")}, smsMessageSid: ${json.getString("smsMessageSid")}")
        } catch (ex: SmsException) {
            logger.error("tplId: $tplId, errorMsg: ${ex.message}")
            throw ex
        }
    }

    fun sendCaptcha(mobile: String, data: Array<String>) = sendSmsByTplId(mobile, rongLianProps.smsTpls.captchaId, data)

    fun sendConsumeSuccess(mobile: String, data: Array<String>) = sendSmsByTplId(mobile, rongLianProps.smsTpls.consumeSuccessId, data)

    fun sendDeliverySuccess(mobile: String, data: Array<String>) = sendSmsByTplId(mobile, rongLianProps.smsTpls.deliverySuccessId, data)

}