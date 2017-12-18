package com.menuxx.weixin.auth

import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import java.net.URLEncoder
import java.util.regex.Pattern
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/13
 * 微信: yin80871901
 */

val WXUrlOAuthState = "sxauth"

class WebAuthenticationEntryPoint(private val wxAppId: String) : AuthenticationEntryPoint {

    private val logger = LoggerFactory.getLogger(WebAuthenticationEntryPoint::class.java)

    private val LoginUrl = "/auth/login"

    private val WeixinMobileAuthorizeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect"
    private val WeixinPCAuthorizeUrl = "https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect"

    private fun getWeixinRedirect(urlTpl: String, encodeFormUrl: String) : String {
        return urlTpl
                .replace("APPID", wxAppId)
                .replace("REDIRECT_URI", encodeFormUrl)
                .replace("SCOPE", "snsapi_userinfo")
                .replace("STATE", WXUrlOAuthState)
    }

    /**
     * 如果该用户没有授权，会执行到此处
     */
    override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
        val ua = request.getHeader("User-Agent")
        // 当前页面的 url
        val fromUrl = request.requestURL.toString()
        // 微信 UserAgent
        val wxUa = Pattern.compile("MicroMessenger")
        // 手机
        val mobilePattern = Pattern.compile("Mobile")
        when( true ) {
            // 如果在微信中打开
            wxUa.matcher(ua).find() -> {
                logger.info("redirect: $fromUrl")
                val encodeUrl = URLEncoder.encode(fromUrl, "UTF-8")
                // 并且在手机中打开
                if (mobilePattern.matcher(ua).find()) {
                    val redUrl = getWeixinRedirect(WeixinMobileAuthorizeUrl, encodeUrl)
                    logger.info("redirectTo: $redUrl, in mobile")
                    response.sendRedirect(redUrl)
                    // 否则都是用 pc 授权链接
                } else {
                    val redUrl = getWeixinRedirect(WeixinPCAuthorizeUrl, encodeUrl)
                    logger.info("in pc redirect: $redUrl, in pc")
                    response.sendRedirect(redUrl)
                }
            }
            else -> {
                // 否则都重定向到登录页面
                response.sendRedirect(LoginUrl)
            }
        }

    }

}