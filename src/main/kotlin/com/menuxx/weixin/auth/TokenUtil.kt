package com.menuxx.weixin.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import java.nio.charset.Charset
import java.util.Date

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/18
 * 微信: yin80871901
 */

class TokenProcessor(val secret: String, val expiration: Int) {

    private val logger = LoggerFactory.getLogger(TokenProcessor::class.java)

    /**
     * 获取所有 Claims
     */
    fun getClaimsFromToken(token: String) : Claims? {
        return try {
            Jwts.parser()
                    .setSigningKey(secret.toByteArray(Charset.forName("UTF-8")))
                    .parseClaimsJws(token)
                    .body
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    /**
     * 获取 openid
     */
    fun getOpenIdFromToken(token: String): String? {
        return try {
            getClaimsFromToken(token)?.subject
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    /**
     * 获取 token 的创建日期
     */
    fun getCreatedDateFromToken(token: String) : Date? {
        return try {
            Date(getClaimsFromToken(token)?.get("created") as Long)
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    /**
     * 获取 token 的过期时间
     */
    fun getExpirationDateFromToken(token: String) : Date? {
        return try {
            getClaimsFromToken(token)?.expiration
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    fun getAudienceFromToken(token: String) : String? {
        return try {
            getClaimsFromToken(token)?.get("audience") as String
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    private fun generateCurrentDate(): Date {
        return Date(System.currentTimeMillis())
    }

    /**
     * 得到过期时间
     */
    private fun genExpirationDate() : Date {
        return Date(System.currentTimeMillis() + expiration * 1000)
    }

    /**
     * token 是否过期
     */
    fun isTokenExpired(token: String) : Boolean {
        return getExpirationDateFromToken(token)?.before(generateCurrentDate()) ?: true
    }

    /**
     * 生成 token
     * openId 用户在微信中的 openid
     * ipAddress 用户客户端的 ip 地址
     */
    fun genToken(openId: String, ipAddress: String) : String {
        val claims = hashMapOf("sub" to openId, "audience" to ipAddress, "created" to generateCurrentDate())
        return generateToken(claims)
    }

    /**
     * 刷新 token 令牌
     * 原药原来的 token
     * 会刷新创建时间
     */
    fun refreshToken(token: String): String? {
        return try {
            val claims = getClaimsFromToken(token) ?: return null
            claims.put("created", generateCurrentDate())
            return generateToken(claims.toMap())
        } catch (e: Exception) {
            null
        }
    }

    private fun generateToken(claims: Map<String, Any>) : String {
        return try {
            Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(genExpirationDate())
                    .signWith(SignatureAlgorithm.HS512, secret.toByteArray(Charset.forName("UTF-8")))
                    .compact()
        } catch (ex: Exception) {
            //didn't want to have this method throw the exception, would rather log it and sign the token like it was before
            logger.warn(ex.message)
            return Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(genExpirationDate())
                    .signWith(SignatureAlgorithm.HS512, secret)
                    .compact()
        }
    }

    /**
     * 验证 token
     * 1. 验证 openid 是否一致
     * 2. 验证 是否过期
     */
    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val authUser = userDetails as AuthUser
        val openId = getOpenIdFromToken(token)
        return openId == authUser.openid && !this.isTokenExpired(token)
    }


}