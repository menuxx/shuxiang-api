package com.menuxx.common.db

import com.menuxx.common.bean.Area
import com.menuxx.common.db.tables.TArea
import org.jooq.DSLContext
import org.springframework.stereotype.Service

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/4
 * 微信: yin80871901
 */
@Service
class AreaDb(private val dsl: DSLContext) {

    val LevelProvincePID = 0

    /**
     * 获取所有省级区域
     */
    fun loadProvinces() = loadAreasByPid(LevelProvincePID)

    /**
     * 获取区域列表通过pid
     */
    fun loadAreasByPid(pid: Int) : List<Area> {
        val tArea = TArea.T_AREA
        return dsl.select().from(tArea).where(tArea.PID.eq(pid)).orderBy(tArea.SORT_WEIGHT.desc()).fetchArray().map {
            it.into(Area::class.java)
        }
    }

}