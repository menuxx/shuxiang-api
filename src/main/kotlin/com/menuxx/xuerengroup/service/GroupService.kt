package com.menuxx.xuerengroup.service

import com.menuxx.code.bean.SXItemCode
import com.menuxx.code.bean.SXItemCodeConsumed
import com.menuxx.code.mongo.ItemCodeRepository
import com.menuxx.common.db.GroupDb
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/16
 * 微信: yin80871901
 */

@RestController
@RequestMapping
class GroupService(
        private val codeRepository: ItemCodeRepository,
        private val groupDb: GroupDb
) {

    /**
     * 将一个 item 消费到一个 group， 从而形成一个 group_user 关系
     */
    @Transactional
    fun consumeItemCodeToGroup(itemCode: SXItemCode, userId: Int, groupId: Int, channel: String) : Int {
        // 能到达 SXItemCodeConsumed 状态
        val willCan = codeRepository.canToStatus(itemCode.code, SXItemCodeConsumed)
        if ( willCan ) {
            codeRepository.updateCodeToConsume(itemCode.code, itemCode.salt, channel, userId)
            groupDb.insertUserToGroup(userId = userId, groupId = groupId, code = itemCode.code, itemId = itemCode.itemId!!)
            return 2
        }
        return 1
    }
}