package com.menuxx.mall.ctrl

import com.fasterxml.jackson.databind.ObjectMapper
import com.menuxx.mall.service.OpenApiService
import com.menuxx.mall.WebApiService
import com.menuxx.mall.YouHaoWebApi
import com.menuxx.mall.exception.YouHaoException
import com.menuxx.mall.getYhsdCustomerSession
import com.menuxx.sso.bean.ApiResp
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import org.springframework.web.servlet.HandlerMapping
import java.io.IOException
import java.net.URLDecoder
import java.io.UnsupportedEncodingException
import java.util.LinkedHashMap
import kotlin.collections.HashMap


/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/18
 * 微信: yin80871901
 */

@RestController
@RequestMapping("/youhao_api")
class YouHaoApiCtrl (
        private val webApiService: WebApiService,
        private val openApiService: OpenApiService,
        private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(YouHaoApiCtrl::class.java)

    private fun mapToHeaders(map: Map<String, String>) : HttpHeaders {
        val newMap = hashMapOf<String, List<String>>()
        // map copy to newMap
        map.forEach { entry -> newMap[entry.key] = listOf(entry.value) }
        val headers = HttpHeaders()
        headers.putAll(newMap)
        return headers
    }

    /**
     * 友好速搭开放 Api
     */
    fun wrapYouHaoOpenApi(method: String, path: String, queryData: Map<String, String>?, data: Map<String, Any>?) : ResponseEntity<Map<String, Any?>> {
        logger.debug("YouHaoOpenApiHandler { method => $method, path => $path}")
        val res = openApiService.request(method, path, queryData, data)
        return ResponseEntity
                .status(res.statusCode)
                .headers(mapToHeaders(res.header))
                .body(objectMapper.readValue(res.body, HashMap::class.java) as HashMap<String, Any>)
    }

    @Throws(UnsupportedEncodingException::class)
    fun splitQuery(queryString: String): HashMap<String, String> {
        val queryPairs = LinkedHashMap<String, String>()
        val pairs = queryString.split("&".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        for (pair in pairs) {
            val idx = pair.indexOf("=")
            queryPairs[URLDecoder.decode(pair.substring(0, idx), "UTF-8")] = URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
        }
        return queryPairs
    }
    /**
     * 友好速搭非开放 WebApi
     */
    fun wrapYouHaoWebApi(method: String, path: String, queryData: HashMap<String, String>?, data: HashMap<String, Any>?, customerSession: String) : ResponseEntity<Map<String, Any?>> {
        logger.debug("YouHaoWebApiHandler { method => $method, path => $path, customerSession => $customerSession }")
        val accessToken = openApiService.getAuthTokenFromCache()
        return try {
            val resData = webApiService.authRequest(method = method, path = path, customerSession = customerSession, accessToken = accessToken, queryData = queryData, data = data)
            ResponseEntity.status(200).body(resData)
        } catch (e: IOException) {
            ResponseEntity.status(502).body(ApiResp(502, "未知异常, 无法访问后端API服务器, 网络错误").toMap())
        } catch (e: YouHaoException) {
            ResponseEntity.status(501).body(ApiResp(e.code, e.message ?: "未知异常").toMap())
        } catch (e: Exception) {
            logger.error("请求友好速搭 webapi 时发生未知异常")
            ResponseEntity.status(500).body(ApiResp(500, "未知异常").toMap())
        }
    }

    fun checkMethod(currMethod: String, method: String) {
        if (currMethod != method) {
            throw HttpRequestMethodNotSupportedException(method, listOf(currMethod))
        }
    }

    private fun getRequestData(request: HttpServletRequest) : HashMap<String, Any> {
        return objectMapper.readValue(request.inputStream, HashMap::class.java) as HashMap<String, Any>
    }

    private fun getQueryData(request: HttpServletRequest) : HashMap<String, String>? {
        return try {
            if ( StringUtils.isNotBlank(request.queryString) ) {
                return splitQuery(request.queryString)
            }
            null
        } catch (ex: UnsupportedEncodingException) {
            logger.warn("输入了非 UTF-8字符集", ex)
            null
        }
    }

    @RequestMapping(value = ["**"], method=[RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT])
    fun handlerYouHaoApi(request: HttpServletRequest) : ResponseEntity<Map<String, Any?>> {
        val restOfTheUrl = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE) as String
        val method = request.method.toUpperCase()
        val path = restOfTheUrl.replace("/youhao_api/", "")
        val customerSession = getYhsdCustomerSession()
        // 检查接口是否正确调用
        return when (path) {
            YouHaoWebApi.CartCreate.path,
            YouHaoWebApi.AddressCreate.path,
            YouHaoWebApi.CartSet.path
            -> {
                checkMethod("POST", method)
                wrapYouHaoWebApi(method, path, getQueryData(request), getRequestData(request), customerSession)
            }
            YouHaoWebApi.WithinShipments.path,
            YouHaoWebApi.Cart.path,
            YouHaoWebApi.Address.path,
            YouHaoWebApi.CartDiscount.path
            -> {
                checkMethod("GET", method)
                wrapYouHaoWebApi(method, path, getQueryData(request), null, customerSession)
            } else -> {
                if ( method == "GET" || method == "DELETE" ) {
                    wrapYouHaoOpenApi(method, path, getQueryData(request), null)
                } else if ( method == "POST" || method == "PUT" ) {
                    wrapYouHaoOpenApi(method, path, getQueryData(request), getRequestData(request))
                } else {
                    throw HttpRequestMethodNotSupportedException(method, listOf("GET", "DELETE", "POST", "PUT"))
                }
            }
        }
    }


}