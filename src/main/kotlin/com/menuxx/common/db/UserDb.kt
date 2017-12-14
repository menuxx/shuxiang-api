package com.menuxx.common.db

import com.menuxx.common.bean.Authority
import com.menuxx.common.bean.User
import com.menuxx.common.bean.UserAuthority
import com.menuxx.common.bean.WXUser
import com.menuxx.common.db.tables.TAuthority
import com.menuxx.common.db.tables.TUser
import com.menuxx.common.db.tables.TUserAuthority
import com.menuxx.common.db.tables.TWxUser
import com.menuxx.weixin.exception.UserNotExistsException
import org.jooq.DSLContext
import org.jooq.types.UInteger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/14
 * 微信: yin80871901
 */

val AuthoritiyRoleUser = 1
val AuthoritiyRoleMerchant = 2
val AuthoritiyRoleAdmin = 3

@Service
class UserDb(private val dsl: DSLContext) {

    /**
     * 根据 openid 获取用户信息
     */
    fun findUserByOpenid(openid: String) : User? {
        val tUser = TUser.T_USER
        val tWxUser = TWxUser.T_WX_USER
        return dsl.select().from(tUser)
                .leftJoin(tWxUser)
                .on(tUser.WX_USER_ID.eq(tWxUser.ID))
                .where(tWxUser.OPENID.eq(openid)).fetchOne()?.into(User::class.java)
    }

    /**
     * 更新微信用户
     */
    private fun updateWXUser(wxUser: WXUser) : Int {
        val tWxUser = TWxUser.T_WX_USER
        return dsl.update(tWxUser).set(dsl.newRecord(tWxUser, wxUser)).where(tWxUser.ID.eq(UInteger.valueOf(wxUser.id))).execute()
    }

    /**
     * 更新用户
     */
    private fun updateUser(user: User) : Int {
        val tUser = TUser.T_USER
        return dsl.update(tUser).set(dsl.newRecord(tUser, user)).where(tUser.ID.eq(UInteger.valueOf(user.id))).execute()
    }

    /**
     * 插入微信用户
     */
    private fun insertWXUser(wxUser: WXUser) : WXUser {
        val tWxUser = TWxUser.T_WX_USER
        return dsl.insertInto(tWxUser).set(dsl.newRecord(tWxUser, wxUser)).returning().fetchOne().into(WXUser::class.java)
    }

    /**
     * 获取用户授权信息详情
     */
    fun findAuthoritiesByUserId(userId: Int) : List<UserAuthority> {
        val tUserAuthority = TUserAuthority.T_USER_AUTHORITY
        val tAuthority = TAuthority.T_AUTHORITY
        return dsl.select().from(tUserAuthority)
                .leftJoin(tAuthority).on(tAuthority.ID.eq(tUserAuthority.AUTHORITY_ID))
                .where(tUserAuthority.USER_ID.eq(userId))
                .fetchArray().map {
            // 将对象填充完整
            val userAuth = it.into(UserAuthority::class.java)
            userAuth.authority = it.into(Authority::class.java)
            userAuth
        }
    }

    /**
     * 插入一条授权规则
     */
    private fun insertUserAuthority(authority: UserAuthority) : UserAuthority {
        val tUserAuthority = TUserAuthority.T_USER_AUTHORITY
        return dsl.insertInto(tUserAuthority)
                .set(dsl.newRecord(tUserAuthority, authority))
                .returning()
                .fetchOne().into(UserAuthority::class.java)
    }

    /**
     * 插入系统用户
     */
    private fun insertUser(user: User) : User {
        val tUser = TUser.T_USER
        return dsl.insertInto(tUser).set(dsl.newRecord(tUser, user)).returning().fetchOne().into(User::class.java)
    }

    /**
     * 创建用户
     */
    @Transactional
    fun createUserFromWXUser(wxUser: WXUser, fromIp: String) : User {
        val newSysUser = insertWXUser(wxUser)
        val newUser = User()
        newUser.userName = wxUser.nickname
        newUser.avatarUrl = wxUser.headimgurl
        newUser.lastLoginIp = fromIp
        newUser.lastLoginTime = Date()
        newUser.createAt = Date()
        newUser.enable = 1
        newUser.updateAt = Date()
        newUser.createAt = Date()
        newUser.wxUserId = newSysUser.id
        val user = insertUser(newUser)
        // 该用户的身份为 普通用户
        insertUserAuthority(UserAuthority(1, user.id))
        return user
    }

    /**
     * 更新用户
     */
    @Transactional
    fun updateUserFromWXUser(wxUser: WXUser, fromIp: String) : User {
        val updateOk = updateWXUser(wxUser) == 1
        if ( updateOk ) {
            val user = findUserByOpenid(wxUser.openid)!!
            user.userName = wxUser.nickname
            user.avatarUrl = wxUser.headimgurl
            user.lastLoginIp = fromIp
            user.lastLoginTime = Date()
            updateUser(user)
            return user
        }
        throw UserNotExistsException("id: ${wxUser.id}, nickname: ${wxUser.nickname} is not exists in system")
    }

    /**
     * 如果不存在就创建，如果存在就更新
     */
    fun saveUserFromWXUser(wxUser: WXUser, fromIp: String) : User {
        val user = findUserByOpenid(wxUser.openid)
        // 不存在 就创建
        return if (user == null) {
            createUserFromWXUser(wxUser, fromIp)
            // 更新用户
        } else {
            updateUserFromWXUser(wxUser, fromIp)
        }
    }

}