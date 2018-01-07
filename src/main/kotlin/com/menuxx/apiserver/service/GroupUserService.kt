package com.menuxx.apiserver.service

import com.menuxx.code.mongo.ItemCodeRepository
import org.springframework.stereotype.Service

@Service
class GroupUserService (
        private val itemCodeRepository: ItemCodeRepository
) {

    fun consumeItemCode(userId: Int, code: String, salt: String) {
        val itemCode = itemCodeRepository.getItemCodeDataByCode(code)
        if ( itemCode.salt != salt ) {
        }
        // 更新码到消费状态
        itemCodeRepository.updateCodeToConsume(code, salt, userId)
    }

}