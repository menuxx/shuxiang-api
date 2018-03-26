package com.menuxx.mall.ctrl

import com.menuxx.AllOpen
import com.menuxx.common.db.UserDb
import com.menuxx.getCurrentUser
import com.menuxx.mall.WebApiService
import com.menuxx.mall.service.YhsdAccountService
import com.menuxx.mall.setYhsdCustomerSession
import com.menuxx.sso.bean.ApiResp
import com.menuxx.weixin.exception.UnionidException
import org.apache.commons.lang3.StringUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpSession

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/25
 * 微信: yin80871901
 */

@AllOpen
@RestController
@RequestMapping("/mall")
class AccountCtrl (
    private val webApiService: WebApiService,
    private val userDb: UserDb,
    private val yhsdAccountService: YhsdAccountService
) {

    @PostMapping("/miniapp_to_login")
    fun login(session: HttpSession) : ResponseEntity<ApiResp> {
        val user = getCurrentUser()
        if ( user.unionid != null ) {
            val dbUser = userDb.findUserByUnionId(user.unionid)
            if ( dbUser == null ) {
                throw UnionidException("用户不存在 unionid, 无法完成对商城的绑定")
            } else {
                val loginSession = if ( StringUtils.isNotBlank(dbUser.yhEmail) && StringUtils.isNotBlank(dbUser.yhPassword)  ) {
                    webApiService.accountLogin(account = dbUser.yhEmail, password = dbUser.yhPassword)
                } else {
                    val customer = yhsdAccountService.registerAccount(dbUser, user.unionid)
                    webApiService.accountLogin(account = customer.regIdentity, password = customer.password!!)
                }
                // 产生 用户 登录 session id
                setYhsdCustomerSession(loginSession["token"] as String)
            }
        }
        throw UnionidException("用户不存在 unionid, 无法完成对商城的绑定")
    }
}