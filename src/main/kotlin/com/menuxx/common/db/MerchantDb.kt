package com.menuxx.common.db

import com.menuxx.apiserver.auth.AuthUserTypeMerchant
import com.menuxx.common.bean.Authority
import com.menuxx.common.bean.MerchantUser
import com.menuxx.common.bean.UserAuthority
import com.menuxx.common.db.tables.TAuthority
import com.menuxx.common.db.tables.TMerchantUser
import com.menuxx.common.db.tables.TUserAuthority
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/23
 * 微信: yin80871901
 */

@Service
class MerchantDb(private val dsl: DSLContext) {

    private final val logger = LoggerFactory.getLogger(MerchantDb::class.java)

    private final val tMerchant = TMerchantUser.T_MERCHANT_USER
    private final val tUserAuthority = TUserAuthority.T_USER_AUTHORITY
    private final val tAuthority = TAuthority.T_AUTHORITY

    fun findMerchantByPhoneNumber(phoneNumber: String) : MerchantUser? {
        return dsl.select().from(tMerchant).where(tMerchant.PHONE_NUMBER.eq(phoneNumber)).fetchOne()?.into(MerchantUser::class.java)
    }

    fun updateMerchantCaptchaByPhoneNumber(phoneNumber: String, captcha: String) : Int {
        return dsl.update(tMerchant).set(tMerchant.CAPTCHA, captcha).where(tMerchant.PHONE_NUMBER.eq(phoneNumber)).execute()
    }

    fun findAuthoritiesByMerchantId(merchantId: Int) : List<UserAuthority> {
        return dsl.select().from(tUserAuthority)
                .leftJoin(tAuthority).on(tAuthority.ID.eq(tUserAuthority.AUTHORITY_ID))
                .where(tUserAuthority.USER_ID.eq(merchantId).and(tUserAuthority.USER_TYPE.eq(AuthUserTypeMerchant)))
                .fetchArray().map {
            // 将对象填充完整
            val userAuth = it.into(tUserAuthority).into(UserAuthority::class.java)
            userAuth.authority = it.into(tAuthority).into(Authority::class.java)
            userAuth
        }
    }

}