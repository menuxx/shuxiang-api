package com.menuxx.weixin.ctrl

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.binarywang.wxpay.service.WxPayService
import com.google.common.io.ByteStreams
import com.menuxx.common.bean.OrderCharge
import com.menuxx.miaosha.queue.MsgTags
import com.menuxx.weixin.queue.publisher.TradeOrderPublisher
import org.slf4j.LoggerFactory
import org.springframework.amqp.AmqpException
import org.springframework.web.bind.annotation.*
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
        private val tradeOrderPublisher : TradeOrderPublisher,
        private val wxPayService: WxPayService,
        private val objectMapper: ObjectMapper
) {

    private final val logger = LoggerFactory.getLogger(WeiXinEventHandlerCtrl::class.java)

    @PostMapping("/pay_notify/{tag}")
    fun payNotify(request: HttpServletRequest, @PathVariable tag: String) : String {
        val notifyBody = String(ByteStreams.toByteArray(request.inputStream), Charset.forName("UTF-8"))
        val result = wxPayService.parseOrderNotifyResult(notifyBody)
        // 在系统中 OrderCharge 是 WxPayOrderNotifyResult 的子集，可以收集 WxPayOrderNotifyResult 中的大部分数据
        // WxPayOrderNotifyResult => OrderCharge
        val msgBody = objectMapper.writeValueAsBytes(result)
        val orderCharge = objectMapper.readValue<OrderCharge>(msgBody, OrderCharge::class.java)
        return try {
            when ( tag ) {
                MsgTags.TagConsumeObtain -> tradeOrderPublisher.sendTradeOrderWithObtainConsume(orderCharge)
            }
            "SUCCESS"
        } catch (e: AmqpException) {
            logger.error("payNotify", e)
            "FAIL"
        }
    }

}