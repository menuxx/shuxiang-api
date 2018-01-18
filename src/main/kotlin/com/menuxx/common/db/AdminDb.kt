package com.menuxx.common.db

import com.menuxx.sso.auth.AuthUserTypeAdmin
import com.menuxx.common.bean.AdminUser
import com.menuxx.common.bean.Authority
import com.menuxx.common.bean.UserAuthority
import com.menuxx.common.db.tables.TAdminUser
import com.menuxx.common.db.tables.TAuthority
import com.menuxx.common.db.tables.TUserAuthority
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant

@Service
class AdminDb(private val dsl: DSLContext) {

    private final val tAdminUser = TAdminUser.T_ADMIN_USER

    private final val tUserAuthority = TUserAuthority.T_USER_AUTHORITY

    private final val tAuthority = TAuthority.T_AUTHORITY

    fun findUserByUsername(userName: String) : AdminUser? {
        return dsl.select().from(tAdminUser).where(tAdminUser.USERNAME.eq(userName)).fetchOneInto(AdminUser::class.java)
    }

    /**
     * 根据 管理员 id 获取权限列表
     */
    fun findAuthoritiesByAdminId(adminId: Int) : List<UserAuthority> {
        return dsl.select().from(tUserAuthority)
                .leftJoin(tAdminUser).on(tAdminUser.ID.eq(tUserAuthority.AUTHORITY_ID))
                .where(tUserAuthority.USER_ID.eq(adminId).and(tUserAuthority.USER_TYPE.eq(AuthUserTypeAdmin)))
                .fetchArray().map {
            // 将对象填充完整
            val userAuth = it.into(tUserAuthority).into(UserAuthority::class.java)
            userAuth.authority = it.into(tAuthority).into(Authority::class.java)
            userAuth
        }
    }

    fun loginUpdate(lastLoginIp: String) : Int {
        return dsl.update(tAdminUser)
                .set(tAdminUser.LAST_LOGIN_IP, lastLoginIp)
                .set(tAdminUser.LAST_LOGIN_TIME, Timestamp.from(Instant.now()))
                .execute()
    }

}