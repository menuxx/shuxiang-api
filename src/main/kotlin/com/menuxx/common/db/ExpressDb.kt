package com.menuxx.common.db

import com.menuxx.common.bean.Express
import com.menuxx.common.db.tables.TExpress
import org.jooq.DSLContext
import org.springframework.stereotype.Service

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/23
 * 微信: yin80871901
 */
@Service
class ExpressDb(private val dsl: DSLContext) {

    private val tExpress = TExpress.T_EXPRESS

    fun loadExpresses() : List<Express> {
        return dsl.select().from(tExpress).fetchArray().map {
            it.into(Express::class.java)
        }
    }

}