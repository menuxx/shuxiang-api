package com.menuxx.sso.ctrl

import com.menuxx.Page
import com.menuxx.PageParam
import com.menuxx.code.db.ItemCodeTaskDb
import com.menuxx.common.bean.ItemCodeTask
import org.springframework.web.bind.annotation.*

@RequestMapping("code_tasks")
@RestController
class CodeTaskCtrl (private val codeTaskDb: ItemCodeTaskDb) {

    @GetMapping
    fun loadCodeTask(@RequestParam(defaultValue = Page.DefaultPageNumText) pageNum: Int, @RequestParam(defaultValue = Page.DefaultPageSizeText) pageSize: Int) : List<ItemCodeTask> {
        return codeTaskDb.loadTask(PageParam(pageNum, pageSize))
    }

    @GetMapping("/{taskId}")
    fun getCodeTask(@PathVariable taskId: Int) : ItemCodeTask {
        return codeTaskDb.getTaskDetails(taskId)
    }

}