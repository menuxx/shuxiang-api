package com.menuxx.mall.bean

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/23
 * 微信: yin80871901
 */

data class YhsdCustomer(
        @JsonProperty("id")
        val id: Int?,
        @JsonProperty("name")
        val name: String,
        @JsonProperty("reg_type")
        val regType: String,
        @JsonProperty("reg_identity")
        val regIdentity: String,
        @JsonProperty("password")
        val password: String?,
        @JsonProperty("notify_email")
        val notifyEmail: String,
        @JsonProperty("notify_phone")
        val notifyPhone: String?,
        @JsonProperty("identity_card")
        val identityCard: String?,
        @JsonProperty("real_name")
        val realName: String,
        @JsonProperty("customer_level_name")
        val customerLevelName: String?
)