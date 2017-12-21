package com.menuxx.weixin.ctrl

import com.aliyun.openservices.ons.api.Message
import com.aliyun.openservices.ons.api.OnExceptionContext
import com.aliyun.openservices.ons.api.SendCallback
import com.aliyun.openservices.ons.api.SendResult
import com.aliyun.openservices.ons.api.bean.ProducerBean
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.binarywang.wxpay.service.WxPayService
import com.menuxx.common.prop.AliyunProps
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.async.DeferredResult

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/13
 * 微信: yin80871901
 * 参考： https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7
 * 微信事件回调 支付回调
 */
@RequestMapping("/weixin_event_handler")
@RestController
class WeiXinEventHandlerCtrl(
        @Autowired @Qualifier("wxPayMsgProducer") private val wxPayMsgProducer: ProducerBean,
        private val wxPayService: WxPayService,
        private val aliyunProps: AliyunProps,
        private val objectMapper: ObjectMapper
) {

    @PostMapping(path = ["/pay_notify"], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun payNotify(@RequestBody orderNotifyEventOfString: String, @RequestParam("tag") tag: String) : DeferredResult<String> {
        val result = wxPayService.parseOrderNotifyResult(orderNotifyEventOfString)
        val msg = Message()
        msg.topic = aliyunProps.ons.publicTopic
        msg.tag = tag
        msg.key = "WeiXinOrderPay_${result.outTradeNo}"
        msg.body = objectMapper.writeValueAsBytes(result)
        val asyncResult = DeferredResult<String>()
        wxPayMsgProducer.sendAsync(msg, object : SendCallback {
            override fun onSuccess(sendResult: SendResult?) {
                asyncResult.setResult("SUCCESS")
            }
            override fun onException(context: OnExceptionContext) {
                asyncResult.setErrorResult("FAIL")
            }
        })
        return asyncResult
    }

}