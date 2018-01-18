package com.menuxx.common.db

import com.menuxx.PageParam
import com.menuxx.common.bean.GroupTopic
import com.menuxx.common.bean.User
import com.menuxx.common.bean.WXUser
import com.menuxx.common.db.tables.TGroupTopic
import com.menuxx.common.db.tables.TUser
import com.menuxx.common.db.tables.TWxUser
import com.menuxx.weixin.util.nullSkipUpdate
import org.jooq.DSLContext
import org.jooq.types.UInteger
import org.springframework.stereotype.Service

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/16
 * 微信: yin80871901
 */
@Service
class GroupTopicDb (private val dsl: DSLContext) {

    private val tGroupTopic = TGroupTopic.T_GROUP_TOPIC
    private val tUser = TUser.T_USER
    private val tWXUser = TWxUser.T_WX_USER

    fun getTopicsByGroupId(groupId: Int, page: PageParam) : List<GroupTopic> {
        return dsl.select()
                .from(tGroupTopic)
                .leftJoin(tUser).on(tUser.ID.eq(tGroupTopic.CREATOR_ID))
                .leftJoin(tWXUser).on(tUser.WX_USER_ID.eq(tWXUser.ID))
                .where(tGroupTopic.GROUP_ID.eq(UInteger.valueOf(groupId)))
                .offset(page.getOffset())
                .limit(page.getLimit())
                .fetchArray().map {
            val topic = it.into(tGroupTopic).into(GroupTopic::class.java)
            topic.creator = it.into(tUser).into(User::class.java)
            topic.creator.wxUser = it.into(tWXUser).into(WXUser::class.java)
            topic
        }
    }

    fun insertTopicToGroup(groupId: Int, topic: GroupTopic) : GroupTopic {
        topic.groupId = groupId
        topic.status = GroupTopic.STATUS_RELEASE
        return dsl.insertInto(tGroupTopic)
                .set(nullSkipUpdate(dsl.newRecord(tGroupTopic, topic)))
                .returning()
                .fetchOne()
                .into(GroupTopic::class.java)
    }

}