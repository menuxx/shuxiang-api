package com.menuxx.weixin.ctrl

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.binarywang.wxpay.service.WxPayService
import com.google.common.io.ByteStreams
import com.menuxx.common.bean.OrderCharge
import com.menuxx.common.exception.NotFoundException
import com.menuxx.miaosha.queue.MsgTags
import com.menuxx.weixin.queue.publisher.TradeOrderPublisher
import org.slf4j.LoggerFactory
import org.springframework.amqp.AmqpException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
        @Qualifier("wxMpPayService") private val wxMpPayService: WxPayService,
        @Qualifier("wxMiniAppPayService") private val wxMiniAppPayService: WxPayService,
        private val objectMapper: ObjectMapper
) {

    private final val logger = LoggerFactory.getLogger(WeiXinEventHandlerCtrl::class.java)

    @PostMapping("/{provider:[A-Za-z0-9]+}_pay_notify/{tag}")
    fun payNotify(request: HttpServletRequest, @PathVariable provider: String, @PathVariable tag: String) : ResponseEntity<String> {
        val notifyBody = String(ByteStreams.toByteArray(request.inputStream), Charset.forName("UTF-8"))
        val payService = when(provider) {
            "mp" -> wxMpPayService
            "mini" -> wxMiniAppPayService
            else -> throw NotFoundException("未找到对应的支付通道")
        }
        logger.info("------- weixin pay xml - begin. ----------")
        logger.info(notifyBody)
        logger.info("------- weixin pay xml - end.  ----------")
        val result = payService.parseOrderNotifyResult(notifyBody)
        // 在系统中 OrderCharge 是 WxPayOrderNotifyResult 的子集，可以收集 WxPayOrderNotifyResult 中的大部分数据
        // WxPayOrderNotifyResult => OrderCharge
        val msgBody = objectMapper.writeValueAsBytes(result)
        val orderCharge = objectMapper.readValue<OrderCharge>(msgBody, OrderCharge::class.java)
        return try {
            when ( tag ) {
                MsgTags.TagConsumeObtain -> tradeOrderPublisher.sendTradeOrderWithObtainConsume(orderCharge)
                MsgTags.TagMallOrderPay -> tradeOrderPublisher.sendTradeMallOrderPay(orderCharge)
            }
            ResponseEntity.ok("SUCCESS")
        } catch (e: AmqpException) {
            logger.error("payNotify", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL")
        }
    }

}