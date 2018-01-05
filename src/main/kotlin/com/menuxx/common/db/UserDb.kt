package com.menuxx.common.db

import com.menuxx.apiserver.auth.AuthUserTypeNormal
import com.menuxx.common.bean.Authority
import com.menuxx.common.bean.User
import com.menuxx.common.bean.UserAuthority
import com.menuxx.common.bean.WXUser
import com.menuxx.common.db.tables.TAuthority
import com.menuxx.common.db.tables.TUser
import com.menuxx.common.db.tables.TUserAuthority
import com.menuxx.common.db.tables.TWxUser
import com.menuxx.weixin.exception.UserNotExistsException
import com.menuxx.weixin.util.nullSkipUpdate
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

    private final val tUser = TUser.T_USER
    private final val tWxUser = TWxUser.T_WX_USER
    private final val tUserAuthority = TUserAuthority.T_USER_AUTHORITY
    private final val tAuthority = TAuthority.T_AUTHORITY

    fun getUserDetailById(userId: Int) : User? {
        val record = dsl.select().from(tUser)
                .leftJoin(tWxUser).on(tUser.WX_USER_ID.eq(tWxUser.ID))
                .where(tUser.ID.eq(UInteger.valueOf(userId)))
                .fetchOne()
        val user = record?.into(tUser)?.into(User::class.java)
        user?.wxUser = record?.into(tWxUser)?.into(WXUser::class.java)
        return user
    }

    /**
     * 根据 openid 获取用户信息
     */
    fun findUserByOpenid(openid: String) : User? {
        val record = dsl.select().from(tUser)
                .leftJoin(tWxUser)
                .on(tUser.WX_USER_ID.eq(tWxUser.ID))
                .where(tWxUser.OPENID.eq(openid)).fetchOne()
        val user = record?.into(tUser)?.into(User::class.java)
        user?.wxUser = record?.into(tWxUser)?.into(WXUser::class.java)
        return user
    }

    /**
     * 根据 unionId 或者 openid 获取用户
     */
    fun findUserByUnionIdOrderOpenId(userNameId: String) : User? {
        val record = dsl.select().from(tUser)
                .leftJoin(tWxUser)
                .on(tUser.WX_USER_ID.eq(tWxUser.ID))
                .where(tWxUser.UNIONID.eq(userNameId).or(tWxUser.OPENID.eq(userNameId)))
                .fetchOne()
        val user = record?.into(tUser)?.into(User::class.java)
        user?.wxUser = record?.into(tWxUser)?.into(WXUser::class.java)
        return user
    }

    fun fetchUserByIdList(idList: List<Int>) : List<User> {
        return dsl.select().from(tUser).where(tWxUser.ID.`in`(idList.map { UInteger.valueOf(it) })).fetchArray().map {
            it.into(User::class.java)
        }
    }

    /**
     * 更新微信用户
     */
    private fun updateWXUserByOpenId(wxUser: WXUser, openid: String) : Int {
        return dsl.update(tWxUser).set( nullSkipUpdate(dsl.newRecord(tWxUser, wxUser)) ).where(tWxUser.OPENID.eq(openid)).execute()
    }

    /**
     * 更新用户
     */
    private fun updateUser(user: User) : Int {
        return dsl.update(tUser).set( nullSkipUpdate(dsl.newRecord(tUser, user)) ).where(tUser.ID.eq(UInteger.valueOf(user.id))).execute()
    }

    /**
     * 插入微信用户
     */
    private fun insertWXUser(wxUser: WXUser) : WXUser {
        return dsl.insertInto(tWxUser).set( nullSkipUpdate(dsl.newRecord(tWxUser, wxUser)) ).returning().fetchOne().into(WXUser::class.java)
    }

    /**
     * 获取用户授权信息详情
     */
    fun findAuthoritiesByUserId(userId: Int) : List<UserAuthority> {
        return dsl.select().from(tUserAuthority)
                .leftJoin(tAuthority).on(tAuthority.ID.eq(tUserAuthority.AUTHORITY_ID))
                .where(tUserAuthority.USER_ID.eq(userId).and(tUserAuthority.USER_TYPE.eq(AuthUserTypeNormal)))
                .fetchArray().map {
            // 将对象填充完整
            val userAuth = it.into(tUserAuthority).into(UserAuthority::class.java)
            userAuth.authority = it.into(tAuthority).into(Authority::class.java)
            userAuth
        }
    }

    /**
     * 插入一条授权规则
     */
    private fun insertUserAuthority(authority: UserAuthority) : UserAuthority {
        return dsl.insertInto(tUserAuthority)
                .set(dsl.newRecord(tUserAuthority, authority))
                .returning()
                .fetchOne().into(UserAuthority::class.java)
    }

    /**
     * 插入系统用户
     */
    private fun insertUser(user: User) : User {
        return dsl.insertInto(tUser).set( nullSkipUpdate(dsl.newRecord(tUser, user)) ).returning().fetchOne().into(User::class.java)
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
        insertUserAuthority(UserAuthority(1, user.id, AuthUserTypeNormal))
        return user
    }

    /**
     * 更新用户
     */
    @Transactional
    fun updateUserFromWXUser(wxUser: WXUser, fromIp: String) : User {
        val updateOk = updateWXUserByOpenId(wxUser, wxUser.openid) == 1
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