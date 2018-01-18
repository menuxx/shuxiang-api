package com.menuxx.common.db

import com.menuxx.PageParam
import com.menuxx.common.bean.Book
import com.menuxx.common.bean.Group
import com.menuxx.common.db.tables.TBook
import com.menuxx.common.db.tables.TGroup
import com.menuxx.weixin.util.nullSkipUpdate
import org.jooq.DSLContext
import org.jooq.types.UInteger
import org.springframework.stereotype.Service

@Service
class BookDb ( private val dsl: DSLContext ) {

    private final val tBook = TBook.T_BOOK
    private final val tGroup = TGroup.T_GROUP

    fun insertBook(book : Book) : Book {
        return dsl.insertInto(tBook).set( nullSkipUpdate(dsl.newRecord(tBook, book)) )
                .returning().fetchOne().into(Book::class.java)
    }

    fun getBookId(bookId: Int) : Book {
        return dsl.select().from(tBook).where(tBook.ID.eq(UInteger.valueOf(bookId))).fetchOneInto(Book::class.java)
    }

    fun findBooksByGroupId(groupId: Int) : List<Book> {
        return dsl.select().from(tBook).where(tBook.GROUP_ID.eq(UInteger.valueOf(groupId))).fetchArray().map {
            it.into(Book::class.java)
        }
    }

    fun loadBooks(page: PageParam) : List<Book> {
        return dsl.select().from(tBook).orderBy(tBook.CREATE_AT.desc())
                .offset(page.getOffset())
                .limit(page.getLimit())
                .fetchArray()
                .map { it.into(Book::class.java) }
    }

    fun getGroupByBookId(bookId: Int) : Group? {
        return dsl.select()
                .from(tBook)
                .leftJoin(tGroup).on(tBook.GROUP_ID.eq(tGroup.ID))
                .where(tBook.ID.eq(UInteger.valueOf(bookId)))
                .fetchOneInto(Group::class.java)
    }

}