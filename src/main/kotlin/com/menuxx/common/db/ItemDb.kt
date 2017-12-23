package com.menuxx.common.db

import com.menuxx.Const
import com.menuxx.apiserver.PageParam
import com.menuxx.common.bean.Item
import com.menuxx.common.db.tables.TItem
import com.menuxx.weixin.util.nullSkipUpdate
import org.jooq.DSLContext
import org.jooq.types.UInteger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/5
 * 微信: yin80871901
 */
@Service
class ItemDb (private val dsl: DSLContext) {

    private final val tItem = TItem.T_ITEM

    /**
     * 新增一个书籍
     */
    fun insetItem(item: Item) : Item {
        return dsl.insertInto(tItem)
                .set(nullSkipUpdate(dsl.newRecord(tItem, item)))
                .returning().fetchOne().into(Item::class.java)
    }

    /**
     * 更新一个 书籍
     */
    @Transactional
    fun updateItem(itemId: Int, item: Item) : Item {
        dsl.update(tItem)
                    .set(nullSkipUpdate(dsl.newRecord(tItem, item)))
                    // 过滤掉 状态为 0 的
                    .where(tItem.ID.eq(UInteger.valueOf(itemId)).and(tItem.STATUS.ne(Const.DbStatusDel)))
                    .execute()
        return getById(itemId)
    }

    /**
     * 获取 出版社 发布的书籍，支持分页
     */
    fun selectItemsOfPage(creatorId: Int, page: PageParam) : List<Item> {
        return dsl.select().from(tItem)
                .where(
                        tItem.MERCHANT_ID.eq(UInteger.valueOf(creatorId))
                        // 过滤掉 状态为 0 的
                        .and(tItem.STATUS.ne(Const.DbStatusDel))
                )
                .orderBy(tItem.CREATE_AT.desc())    // 创建时间倒序排，最新的排在最上面
                .offset(page.getOffset())
                .limit(page.getLimit())
                .fetchArray().map { it.into(Item::class.java) }
    }

    /**
     * 获取 item 详细信息
     */
    fun getById(itemId: Int) : Item {
        return dsl.select().from(tItem).where(
                    tItem.ID.eq(UInteger.valueOf(itemId))
                            .and(tItem.STATUS.ne(Const.DbStatusDel))    // 过滤掉 状态为 0 的
            ).fetchOne().into(Item::class.java)
    }

    fun delById(itemId: Int) : Int {
        return dsl.update(tItem).set(tItem.STATUS, Const.DbStatusDel).where(
                    tItem.ID.eq(UInteger.valueOf(itemId))
            ).execute()
    }

}