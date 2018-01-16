package com.menuxx.common.db

import com.menuxx.PageParam
import com.menuxx.common.bean.Book
import com.menuxx.common.bean.Group
import com.menuxx.common.bean.GroupUser
import com.menuxx.common.db.tables.TBook
import com.menuxx.common.db.tables.TGroup
import com.menuxx.common.db.tables.TGroupUser
import org.jooq.DSLContext
import org.jooq.types.UInteger
import org.springframework.stereotype.Service

@Service
class GroupDb (private val dsl: DSLContext) {

    private val tGroupUser = TGroupUser.T_GROUP_USER
    private val tGroup = TGroup.T_GROUP
    private val tBook = TBook.T_BOOK

    fun findGroupByUserId(userId: Int, page: PageParam) : List<GroupUser> {
        return dsl.select().from(tGroupUser)
                .leftJoin(tGroup).on(tGroup.ID.eq(tGroupUser.GROUP_ID))
                .leftJoin(tBook).on(tBook.ID.eq(tGroupUser.ITEM_ID))
                .where(tGroupUser.USER_ID.eq(UInteger.valueOf(userId)).and(tGroupUser.STATUS.eq(1)))
                .orderBy(tGroupUser.CREATE_AT.desc())
                .limit(page.getLimit())
                .offset(page.getOffset())
                .fetchArray().map {
            val groupUser = it.into(tGroupUser).into(GroupUser::class.java)
            groupUser.group = it.into(tGroup).into(Group::class.java)
            groupUser.item = it.into(tBook).into(Book::class.java)
            groupUser
        }
    }

}