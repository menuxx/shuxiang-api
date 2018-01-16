package com.menuxx.apiserver.ctrl

import com.menuxx.Page
import com.menuxx.PageParam
import com.menuxx.common.bean.Book
import com.menuxx.common.db.BookDb
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/books")
class BookCtrl (private val bookDb: BookDb) {

    @GetMapping
    fun getBooks(@RequestParam(defaultValue = Page.DefaultPageNumText) pageNum: Int, @RequestParam(defaultValue = Page.DefaultPageSizeText) pageSize: Int) : List<Book> {
        return bookDb.loadBooks(PageParam(pageNum, pageSize))
    }

    @PostMapping
    fun insertBook(@Valid @RequestBody book: Book) : Book {
        return bookDb.insertBook(book)
    }

}