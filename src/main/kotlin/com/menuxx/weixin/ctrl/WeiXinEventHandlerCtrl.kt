package com.menuxx.weixin.ctrl

import com.aliyun.openservices.ons.api.Message
import com.aliyun.openservices.ons.api.OnExceptionContext
import com.aliyun.openservices.ons.api.SendCallback
import com.aliyun.openservices.ons.api.SendResult
import com.aliyun.openservices.ons.api.bean.ProducerBean
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.binarywang.wxpay.service.WxPayService
import com.google.common.io.ByteStreams
import com.menuxx.common.prop.AliyunProps
import com.menuxx.miaosha.queue.MsgTags
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.async.DeferredResult
import java.nio.charset.Charset
import javax.servlet.http.HttpServletRequest

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

    @PostMapping("/pay_notify/{tag}")
    fun payNotify(request: HttpServletRequest, @PathVariable tag: String) : DeferredResult<String> {
        val notifyBody = String(ByteStreams.toByteArray(request.inputStream), Charset.forName("UTF-8"))
        val result = wxPayService.parseOrderNotifyResult(notifyBody)
        val msgBody = objectMapper.writeValueAsBytes(result)
        val msg = Message(aliyunProps.ons.payTopicName, MsgTags.TagTradeOrder, "WeiXinOrderPay_${result.outTradeNo}", msgBody)
        msg.putUserProperties("NextTag", tag)
        val asyncResult = DeferredResult<String>()
        wxPayMsgProducer.sendAsync(msg, object : SendCallback {
            override fun onSuccess(sendResult: SendResult) {
                asyncResult.setResult("SUCCESS")
            }
            override fun onException(context: OnExceptionContext) {
                asyncResult.setErrorResult("FAIL")
            }
        })
        return asyncResult
    }

}