package com.menuxx.common.db

import com.menuxx.PageParam
import com.menuxx.common.bean.Book
import com.menuxx.common.bean.Group
import com.menuxx.common.bean.GroupUser
import com.menuxx.common.db.tables.TBook
import com.menuxx.common.db.tables.TGroup
import com.menuxx.common.db.tables.TGroupUser
import com.menuxx.weixin.util.nullSkipUpdate
import org.jooq.DSLContext
import org.jooq.types.UInteger
import org.springframework.stereotype.Service

@Service
class GroupDb (private val dsl: DSLContext) {

    private val tGroupUser = TGroupUser.T_GROUP_USER
    private val tGroup = TGroup.T_GROUP
    private val tBook = TBook.T_BOOK

    /**
     * 讲一个用户和组建立关系
     */
    fun insertUserToGroup(userId: Int, groupId: Int, code: String, itemId: Int) : GroupUser {
        val user = GroupUser()
        user.groupId = groupId
        user.userId = userId
        user.code = code
        user.status = 1
        user.itemId = itemId
        return dsl.insertInto(tGroupUser).set(nullSkipUpdate(dsl.newRecord(tGroupUser, user))).returning().fetchOne().into(GroupUser::class.java)
    }

    fun findGroupUserByGroupIdAndUserId(groupId: Int, userId: Int) : GroupUser? {
        val record = dsl.select().from(tGroupUser)
                .leftJoin(tGroup).on(tGroup.ID.eq(tGroupUser.GROUP_ID))
                .leftJoin(tBook).on(tBook.ID.eq(tGroupUser.ITEM_ID))
                .where(tGroupUser.USER_ID.eq(UInteger.valueOf(userId)).and(tGroupUser.GROUP_ID.eq(UInteger.valueOf(groupId)))).fetchOne()
        if ( record != null ) {
            val groupUser = record.into(tGroupUser).into(GroupUser::class.java)
            groupUser.group = record.into(tGroup).into(Group::class.java)
            groupUser.item = record.into(tBook).into(Book::class.java)
            return groupUser
        }
        return null
    }

    fun getGroupUserByCode(code: String) : GroupUser? {
        val record = dsl.select().from(tGroupUser)
                .leftJoin(tGroup).on(tGroup.ID.eq(tGroupUser.GROUP_ID))
                .leftJoin(tBook).on(tBook.ID.eq(tGroupUser.ITEM_ID))
                .where(tGroupUser.CODE.eq(code)).fetchOne()
        if ( record != null ) {
            val groupUser = record.into(tGroupUser).into(GroupUser::class.java)
            groupUser.group = record.into(tGroup).into(Group::class.java)
            groupUser.item = record.into(tBook).into(Book::class.java)
            return groupUser
        }
        return null
    }

    fun findGroupsByUserId(userId: Int, page: PageParam) : List<GroupUser> {
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

    fun getGroup(groupId: Int) : Group? {
        val sql = dsl.select().from(tGroup)
                .leftJoin(tBook).on(tBook.ID.eq(tGroup.BOOK_ID))
                .where(tGroup.ID.eq(UInteger.valueOf(groupId)))
        val groupRecord = sql.fetchOne()
        val group = groupRecord.into(tGroup).into(Group::class.java)
        group.userCount = sql.count()
        group.book = groupRecord.into(tBook).into(Book::class.java)
        return group
    }

}