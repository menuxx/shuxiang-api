package com.menuxx.common.db

import com.menuxx.PageParam
import com.menuxx.common.bean.Book
import com.menuxx.common.db.tables.TBook
import com.menuxx.weixin.util.nullSkipUpdate
import org.jooq.DSLContext
import org.springframework.stereotype.Service

@Service
class BookDb ( private val dsl: DSLContext ) {

    private final val tBook = TBook.T_BOOK

    fun insertBook(book : Book) : Book {
        return dsl.insertInto(tBook).set( nullSkipUpdate(dsl.newRecord(tBook, book)) )
                .returning().fetchOne().into(Book::class.java)
    }

    fun loadBooks(page: PageParam) : List<Book> {
        return dsl.select().from(tBook).orderBy(tBook.CREATE_AT.desc())
                .offset(page.getOffset())
                .limit(page.getLimit())
                .fetchArray()
                .map { it.into(Book::class.java) }
    }

}