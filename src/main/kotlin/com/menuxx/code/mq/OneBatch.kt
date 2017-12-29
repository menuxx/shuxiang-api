package com.menuxx.code.mq

data class OneBatch(
        val count: Int, // 该批次中的数量
        val startCode: String,  // 开始码
        val endCode: String     // 结束码
)