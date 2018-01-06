package com.menuxx.common.db

import com.menuxx.common.bean.Book
import com.menuxx.common.db.tables.TBook
import org.springframework.stereotype.Service

@Service
class BookDb {

    private final val tBook = TBook.T_BOOK

    fun insertBook (book : Book) {

    }

}