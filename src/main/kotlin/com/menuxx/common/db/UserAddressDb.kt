package com.menuxx.common.db

import com.menuxx.AllOpen
import com.menuxx.Const
import com.menuxx.common.bean.UserAddress
import com.menuxx.common.db.tables.TUserAddress
import org.jooq.DSLContext
import org.jooq.types.UInteger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/15
 * 微信: yin80871901
 */

val UserAddressIsPrimary = 1
val UserAddressNotPrimary = 0

@AllOpen
@Service
class UserAddressDb(private val dsl: DSLContext) {

    private final val tUserAddress = TUserAddress.T_USER_ADDRESS

    fun getAddressById(addressId: Int) : UserAddress? {
        return dsl.select().from(tUserAddress).where(tUserAddress.ID.eq(UInteger.valueOf(addressId))).fetchOne()?.into(UserAddress::class.java)
    }

    fun loadMyAddress(userId: Int) : List<UserAddress> {
        return dsl.select().from(tUserAddress).where(
                // 已删除的就不获取
                tUserAddress.USER_ID.eq(UInteger.valueOf(userId)).and(tUserAddress.STATUS.ne(Const.DbStatusDel))
        ).fetchArray().map {
            it.into(UserAddress::class.java)
        }
    }

    /**
     * 获取关键 address
     */
    fun getPrimaryAddress(userId: Int) : UserAddress? {
        return dsl.select()
                .from(tUserAddress).where(tUserAddress.USER_ID.eq(UInteger.valueOf(userId))
                .and(tUserAddress.PRIMARY.eq(UserAddressIsPrimary))
                .and(tUserAddress.STATUS.ne(Const.DbStatusDel))
        )
                .fetchOne()?.into(UserAddress::class.java)
    }

    /**
     * 创建一个用户地址，当用户没有默认的时候，就把现在插入的地址设置成默认地址
     */
    fun insertAddress(userId: Int, address: UserAddress) : UserAddress {
        val pAddress = getPrimaryAddress(address.userId)
        if ( pAddress != null ) { address.primary = UserAddressIsPrimary }
        return dsl.insertInto(tUserAddress).set(dsl.newRecord(tUserAddress, address)).returning().fetchOne().into(UserAddress::class.java)
    }

    /**
     * 讲一个地址设置成默认
     * 1: 先将该用户的所有地址 从默认设置成为非默认
     * 2: 再将指定地址设置成为默认
     */
    @Transactional
    fun setAddressPrimary(userId: Int, addressId: Int) : Int {
        // 将该用户所有的 地址都设置成非默认
        dsl.update(tUserAddress).set(tUserAddress.PRIMARY, UserAddressNotPrimary).where(tUserAddress.USER_ID.eq(UInteger.valueOf(userId)))
        // 再将需要设置的 地址改成默认
        return dsl.update(tUserAddress).set(tUserAddress.PRIMARY, 1).where(tUserAddress.ID.eq(UInteger.valueOf(addressId))).execute()
    }

    /**
     * 更新用户地址信息
     */
    fun updateAddress(userId: Int, addressId: Int, address: UserAddress) : Int {
        address.userId = userId
        return dsl.update(tUserAddress)
                .set(dsl.newRecord(tUserAddress, address))
                .where(
                        tUserAddress.ID.eq(UInteger.valueOf(addressId))
                                .and(tUserAddress.USER_ID.eq(UInteger.valueOf(userId)))
                )
                .execute()
    }

    /**
     * 删除用户的某个地址，假删除
     */
    fun delAddress(userId: Int, addressId: Int) : Int {
        return dsl.update(tUserAddress).set(tUserAddress.STATUS, Const.DbStatusDel).where(tUserAddress.USER_ID.eq(UInteger.valueOf(userId))).execute()
    }

}