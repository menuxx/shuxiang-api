package com.menuxx.weixin

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/18
 * 微信: yin80871901
 */

@RequestMapping
@RestController
class WXAuthController {

    /**
     * 根据授权 code 获取用户的 会话信息
     */
    @PostMapping("/code_to_session")
    fun codeToSession() {
        
    }

}