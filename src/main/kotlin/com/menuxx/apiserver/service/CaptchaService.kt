package com.menuxx.apiserver.service

import com.aliyun.openservices.ons.api.Message
import com.aliyun.openservices.ons.api.bean.ProducerBean
import com.fasterxml.jackson.databind.ObjectMapper
import com.menuxx.apiserver.exception.NotFoundException
import com.menuxx.apiserver.queue.msg.LoginCaptchaMsg
import com.menuxx.common.db.MerchantDb
import com.menuxx.common.prop.AliyunProps
import com.menuxx.genRandomNumberString4
import com.menuxx.miaosha.queue.MsgTags
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/23
 * 微信: yin80871901
 */

@Service
class CaptchaService(
        private val merchantDb: MerchantDb,
        private val aliyunProps: AliyunProps,
        private val objectMapper: ObjectMapper,
        @Autowired @Qualifier("senderProducer") private val senderProducer: ProducerBean
) {

    @Transactional
    @Throws(NotFoundException::class)
    fun doSendCaptcha(mobile: String) {
        val captcha = genRandomNumberString4()
        val rNum = merchantDb.updateMerchantCaptchaByPhoneNumber(mobile, captcha)
        if ( rNum > 0 ) {
            val msgBody = objectMapper.writeValueAsBytes(LoginCaptchaMsg(mobile = mobile, captchaNo = captcha))
            val msg = Message(aliyunProps.ons.senderTopicName, "SendCaptcha_$mobile", MsgTags.TagSmsSender, msgBody)
            msg.putUserProperties("NextTag", MsgTags.TagCaptchaSms)
            senderProducer.send(msg)
        } else {
            throw NotFoundException("手机号 $mobile 的用户在系统中不存在")
        }
    }

}