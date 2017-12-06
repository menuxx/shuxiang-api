package com.menuxx.ocapiserver.ctrl

import com.menuxx.ocapiserver.AllOpen
import com.menuxx.ocapiserver.Page
import com.menuxx.ocapiserver.PageParam
import com.menuxx.ocapiserver.bean.ApiResp
import com.menuxx.ocapiserver.bean.Item
import com.menuxx.ocapiserver.db.ItemDb
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/4
 * 微信: yin80871901
 */
@AllOpen
@RestController
@RequestMapping("/items")
class ItemCtrl(private val itemDb: ItemDb) {

    /**
     * 加载商家所有商品
     */
    @GetMapping
    fun loadItems(@RequestParam(defaultValue = Page.DefaultPageNumText) pageNum: Int, @RequestParam(defaultValue = Page.DefaultPageSizeText) pageSize: Int) : List<Item> {
        return itemDb.selectItemsOfPage(1, PageParam(pageNum, pageSize))
    }

    /**
     * 创建一个商品到商家
     */
    @PostMapping
    fun addItem(@RequestBody @Valid item: Item) : Item {
        item.merchantId = 1
        return itemDb.insetItem(item)
    }

    /**
     * 获取一个商品详情
     */
    @GetMapping("/{itemId}")
    fun getById(@PathVariable itemId : Int) : Item {
        return itemDb.getById(itemId)
    }

    /**
     * 修改一个商品
     */
    @PutMapping("/{itemId}")
    fun updateItem(@PathVariable itemId: Int, @RequestBody @Valid item: Item) : Item {
        item.merchantId = 1
        return itemDb.updateItem(itemId, item)
    }

    /**
     * 删除一个商品
     */
    @DeleteMapping("/{itemId}")
    fun delItem(@PathVariable itemId: Int) : ApiResp {
        val rowNum = itemDb.delById(itemId)
        return if ( rowNum > 0) {
            ApiResp(1, "delete ok!")
        } else {
            ApiResp(400, "fail, item not found")
        }
    }

}