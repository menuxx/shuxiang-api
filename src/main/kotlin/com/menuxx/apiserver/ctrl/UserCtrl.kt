package com.menuxx.apiserver.ctrl

import com.menuxx.AllOpen
import com.menuxx.Const
import com.menuxx.Page
import com.menuxx.PageParam
import com.menuxx.apiserver.bean.ApiResp
import com.menuxx.common.bean.GroupUser
import com.menuxx.common.bean.Order
import com.menuxx.common.db.GroupDb
import com.menuxx.common.db.OrderDb
import com.menuxx.getCurrentUser
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.regex.Pattern
import javax.validation.Valid

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/14
 * 微信: yin80871901
 */
@AllOpen
@RestController
@RequestMapping("/user")
class UserCtrl (
        private val orderDb: OrderDb,
        private val groupDb: GroupDb
) {

    data class ItemCode(@NotEmpty val code: String)
    @PostMapping
    fun bindGroupByCode(@Valid @RequestBody itemCode: ItemCode) {
        val codeRegExp = Pattern.compile("/~([a-zA-Z0-9]*)~([a-zA-Z0-9]*)/")
        // 1 : 解析 itemCode 得到 code 和 salt
        val matcher = codeRegExp.matcher(itemCode.code)
        matcher.find()
        val code = matcher.group(1)
        val salt = matcher.group(2)

        // 2 : 检查 code 是否为已经绑定状态
        // 3 : 消费 mongodb 中的 code
    }

    @GetMapping("books")
    fun getMyBooks(@RequestParam(defaultValue = Page.DefaultPageNumText) pageNum: Int, @RequestParam(defaultValue = Page.DefaultPageSizeText) pageSize: Int) : List<GroupUser> {
        val user = getCurrentUser()
        return groupDb.findGroupByUserId(user.id, PageParam(pageNum, pageSize))
    }

    @GetMapping("orders/{orderId}")
    fun getOrderWithMe(@PathVariable orderId: Int) : ResponseEntity<Order>? {
        //sessionRepository.createSession()
        val user = getCurrentUser()
        val order = orderDb.getUserOrderDetails(orderId, user.id)
        return if ( order == null ) {
            ResponseEntity.notFound().build<Order>()
        } else {
            ResponseEntity.ok(order)
        }
    }

    @GetMapping("orders")
    fun loadMyOrders(@RequestParam(defaultValue = Page.DefaultPageNumText) pageNum: Int, @RequestParam(defaultValue = Page.DefaultPageSizeText) pageSize: Int) : List<Order> {
        val user = getCurrentUser()
        return orderDb.loadMyOrders(user.id, PageParam(pageNum, pageSize))
    }

    data class ShareImage( @NotEmpty val shareImage: String )
    @PutMapping("/orders/{orderId}/share_image")
    fun updateOrderShareImage(@PathVariable orderId: Int, @Valid @RequestBody image: ShareImage) : ResponseEntity<ApiResp> {
        val rNum = orderDb.updateOrderShareImage(orderId, image.shareImage)
        return if (rNum >= 1) {
            ResponseEntity.ok(ApiResp(Const.NotErrorCode, "ok"))
        } else {
            ResponseEntity.badRequest().body(ApiResp(400, "指定的订单没有更新成功"))
        }
    }

}