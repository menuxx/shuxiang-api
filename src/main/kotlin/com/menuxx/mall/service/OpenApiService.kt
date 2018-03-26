package com.menuxx.mall.service

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.menuxx.mall.bean.YhsdCustomer
import com.menuxx.mall.exception.YouHaoException
import com.menuxx.mall.prop.YouhaoProps
import com.menuxx.mall.toStringMap
import com.youhaosuda.Yhsd
import com.youhaosuda.YhsdResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.SessionCallback
import org.springframework.stereotype.Service
import org.springframework.web.HttpRequestMethodNotSupportedException
import java.util.concurrent.TimeUnit



/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/23
 * 微信: yin80871901
 */

@Service
class OpenApiService(
        yhProp: YouhaoProps,
        @Qualifier("objRedisTemplate") private val objRedisTemplate: RedisTemplate<String, Any>
) {

    private final val ApiTokenCacheKey = "__youhao_api_token_key__"

    private final val logger = LoggerFactory.getLogger(OpenApiService::class.java)

    private val auth = Yhsd.getInstance().auth(yhProp.appKey, yhProp.appSecret, "")

    private val objectMapper = ObjectMapper()

    init {
        objectMapper
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.INDENT_OUTPUT, true).registerModules(KotlinModule()).dateFormat = ISO8601DateFormat()
    }

    private fun getAuthToken() : String {
        val tokenRes = auth.token
        if ( tokenRes.statusCode == 200 ) {
            val data = objectMapper.readValue(tokenRes.body, HashMap::class.java) as HashMap<String, Any>
            if (data["token"] == null) {
                throw YouHaoException(data["code"] as Int, "返回数据中没有 token body: ${tokenRes.body}", data)
            }
            return data["token"] as String
        } else {
            val data = objectMapper.readValue(tokenRes.body, HashMap::class.java) as HashMap<String, Any>
            logger.error("获取 api token 失败，HttpStatusCode: ${tokenRes.statusCode}")
            throw YouHaoException(tokenRes.statusCode, "获取 api token 失败 body: ${tokenRes.body}", data)
        }
    }

    fun getAuthTokenFromCache() : String {
        var token = objRedisTemplate.opsForValue().get(ApiTokenCacheKey)
        return if ( token == null ) {
            token = getAuthToken()
            // 事务添加 一个 token
            objRedisTemplate.execute(object : SessionCallback<String> {
                override fun <K : Any, V : Any> execute(operations: RedisOperations<K, V>): String {
                    val oprs = operations as RedisOperations<String, String>
                    oprs.multi()
                    val opr = operations.boundValueOps(ApiTokenCacheKey)
                    opr.set(token)
                    opr.expire(1800, TimeUnit.SECONDS)
                    oprs.exec()
                    return token
                }
            })
            token
        } else {
            token as String
        }
    }

    /**
     * 重复注册: {"code":422,"errors":{"common":["customer已存在"]}}
     * 正常返回:
     * {
     *  "customer": {
     *  "addresses":[],
     *  "avatar":{},
     *  "created_at":"2018-01-23T20:58:53.327+08:00",
     *  "customer_level_name":"普通会员",
     *  "id":954553,
     *  "identity_card":"3622**********2933",
     *  "last_in":"2018-01-23T20:58:53.327+08:00",
     *  "last_year_reward_point":0,
     *  "name":"for12345",
     *  "notify_email":"for@example.com",
     *  "notify_phone":"13632269380",
     *  "real_name":"张三",
     *  "reg_identity":"for12345@example.com",
     *  "reg_type":"email",
     *  "reward_point":0,
     *  "social_accounts":[],
     *  "social_avatar_url":null,
     *  "social_type":"",
     *  "total_credit":0,
     *  "trade_total_amount":0,
     *  "trade_total_count":0,
     *  "updated_at":"2018-01-23T20:58:53.327+08:00"
     *  }
     *  }
     */
    data class RespCustomer(val customer: YhsdCustomer)
    fun customerRegister(customer: YhsdCustomer) : YhsdCustomer {
        val api = Yhsd.getInstance().api(getAuthTokenFromCache())
        val resp = api.post("customers", "{ \"customer\": " +  objectMapper.writeValueAsString(customer) + " } ")
        if ( resp.statusCode == 200 ) {
            return objectMapper.readValue(resp.body, RespCustomer::class.java).customer
        }
        // error body
        val data = objectMapper.readValue(resp.body, HashMap::class.java)
        throw YouHaoException(data["code"] as Int, "注册用户失败", data["errors"] as HashMap<String, Any>)
    }

    @Throws(HttpRequestMethodNotSupportedException::class)
    fun request(method: String, path: String, queryData: Map<String, String>?, data: Map<String, Any>?) : YhsdResponse {
        val api = Yhsd.getInstance().api(getAuthTokenFromCache())
        return when (method) {
            "GET" -> if (queryData == null) api.get(path) else api.get(path, toStringMap(queryData))
            "DELETE" -> api.delete(path)
            "POST" -> api.post(path, objectMapper.writeValueAsString(data))
            "PUT" -> api.put(path, objectMapper.writeValueAsString(data))
            else -> throw HttpRequestMethodNotSupportedException("请求失败，接口不支持该种请求类型")
        }
    }

}