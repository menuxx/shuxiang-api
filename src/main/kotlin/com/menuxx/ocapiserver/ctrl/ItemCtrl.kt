package com.menuxx.ocapiserver.ctrl

import com.menuxx.ocapiserver.AllOpen
import com.menuxx.ocapiserver.bean.ApiResp
import com.menuxx.ocapiserver.bean.Item
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/4
 * 微信: yin80871901
 */
@RestController
@RequestMapping("/items")
@AllOpen
class ItemCtrl {

    @GetMapping
    fun loadItems() : List<Item> {
        return listOf(
                Item(1, 2, 1, "颠覆者：周鸿祎自传", "rBEhV1Kvv9sIAAAAAAUy3HX-_p0AAG-0wEwBpYABTL0830.jpg"),
                Item(2, 2, 1, "被掩埋的巨人", "58f49747Nfa6808bb.jpg"),
                Item(3, 2, 1, "大秦帝国", "59d767a0Nc964d6ed.jpg"),
                Item(3, 2, 1, "大秦帝国", "5a0038d9N7d41a1a5.jpg"),
                Item(3, 2, 1, "", "rBEQWFEuvNkIAAAAAA1iGwk9blwAABJmANSQDQADWIz534.jpg"),
                Item(3, 2, 1, "", "images/items/599ff650N72088964.jpg")
        )
    }

    @PostMapping
    fun addItem(@RequestBody @Valid item: Item) : Item {
        return Item()
    }

    @GetMapping("/{itemId}")
    fun getById(@PathVariable itemId : Int) : Item {
        return Item()
    }

    @PutMapping("/itemId")
    fun updateItem(@PathVariable itemId: Int) : Item {
        return Item()
    }

    @DeleteMapping("/itemId")
    fun delItem(@PathVariable itemId: Int) : ApiResp {
        return ApiResp(1, "delete ok!")
    }

}
