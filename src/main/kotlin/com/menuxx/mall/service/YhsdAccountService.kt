package com.menuxx.mall.service

import com.menuxx.common.bean.User
import com.menuxx.common.db.UserDb
import com.menuxx.genRandomString
import com.menuxx.mall.bean.YhsdCustomer
import com.menuxx.mall.genAccountEmail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/26
 * 微信: yin80871901
 */
@Service
class YhsdAccountService(
        private val openApiService: OpenApiService,
        private val userDb: UserDb
) {

    @Transactional
    fun registerAccount(user: User, unionid: String) : YhsdCustomer {
        val email = genAccountEmail(unionid)
        val newCustomer = YhsdCustomer(
                id = null,
                name = user.userName,
                regType = "email",
                regIdentity = email,
                password = genRandomString(5),
                notifyEmail = email,
                notifyPhone = user.phoneNumber,
                identityCard = null,
                realName = user.userName,
                customerLevelName = null
        )
        val createdCustomer = openApiService.customerRegister(newCustomer)
        userDb.bindYhsdUser(userId = user.id, yhCustomerId = createdCustomer.id!!, yhPasswd = newCustomer.password!!, yhEmail = email)
        return createdCustomer
    }

}