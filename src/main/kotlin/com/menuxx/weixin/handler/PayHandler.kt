package com.menuxx.weixin.handler

import com.aliyun.openservices.ons.api.bean.ProducerBean
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.*

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/13
 * 微信: yin80871901
 * 参考： https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7
 * 微信事件回调 支付回调
 */



@RequestMapping("weixin_event_callback")
@RestController
class PayHandler(
        @Autowired @Qualifier("wxPayMsgProducer") private val wxPayMsgProducer: ProducerBean,
        private val objectMapper: ObjectMapper
) {

//    @PostMapping(path = ["/pay_notify"], produces = [MediaType.TEXT_PLAIN_VALUE])
//    fun payNotify(@RequestBody payResultEvent: , @RequestParam("tag") tag: String) : DeferredResult<String> {
//
//        val msg = Message()
//        msg.tag = tag
//        msg.key = "KEY_${event.outTradeNo}"
//        msg.body = objectMapper.writeValueAsBytes(event)
//
//        val asyncResult = DeferredResult<String>()
//
//        wxPayMsgProducer.sendAsync(msg, object : SendCallback {
//            override fun onSuccess(sendResult: SendResult?) {
//                asyncResult.setResult("SUCCESS")
//            }
//            override fun onException(context: OnExceptionContext) {
//                asyncResult.setErrorResult("FAIL")
//            }
//        })
//
//        return asyncResult
//    }

}