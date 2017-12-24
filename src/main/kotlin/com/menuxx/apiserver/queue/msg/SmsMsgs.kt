package com.menuxx.apiserver.queue.msg

import com.menuxx.NoArg

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/23
 * 微信: yin80871901
 */
@NoArg
data class DeliverySuccessMsg(
        var mobile: String,
        var expressName: String,
        var expressNo: String
)

@NoArg
data class LoginCaptchaMsg(
        var mobile: String,
        var captchaNo: String
)