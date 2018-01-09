package com.menuxx.apiserver.service

import com.menuxx.apiserver.exception.NotFoundException
import com.menuxx.common.db.MerchantDb
import com.menuxx.common.publisher.SmsPublisher
import com.menuxx.genRandomNumberString4
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
        private val smsPublisher: SmsPublisher
) {

    @Transactional
    @Throws(NotFoundException::class)
    fun doSendCaptcha(mobile: String) {
        val captcha = genRandomNumberString4()
        val rNum = merchantDb.updateMerchantCaptchaByPhoneNumber(mobile, captcha)
        if ( rNum > 0 ) {
            smsPublisher.sendCaptcha(mobile, mobile)
        } else {
            throw NotFoundException("手机号 $mobile 的用户在系统中不存在")
        }
    }

}