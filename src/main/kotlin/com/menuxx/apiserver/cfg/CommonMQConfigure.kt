package com.menuxx.apiserver.cfg

import com.menuxx.common.prop.AliyunProps
import org.springframework.context.annotation.Configuration

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/21
 * 微信: yin80871901
 *
 * 消费者与生产者分开注册
 * 防止出现
 */
@Configuration
class CommonMQConfigure(
        private val aliyunProps: AliyunProps
) {


}