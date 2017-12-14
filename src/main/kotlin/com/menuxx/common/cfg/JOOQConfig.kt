package com.menuxx.common.cfg

import com.menuxx.AllOpen
import com.zaxxer.hikari.HikariDataSource
import org.jooq.ExecuteContext
import org.jooq.SQLDialect
import org.jooq.impl.*
import org.jooq.tools.JooqLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/6
 * 微信: yin80871901
 */
@Configuration
@AllOpen
class JOOQConfig(private val dataSource: HikariDataSource) {

    fun connectionProvider() : DataSourceConnectionProvider {
        return DataSourceConnectionProvider(TransactionAwareDataSourceProxy(dataSource))
    }

    @Bean
    fun dsl() : DefaultDSLContext {
        return DefaultDSLContext(configuration())
    }

    fun configuration() : DefaultConfiguration {
        val jooqCfg = DefaultConfiguration()
        jooqCfg.set(SQLDialect.MYSQL)
        jooqCfg.set(connectionProvider())
        jooqCfg.set(DefaultExecuteListenerProvider(ExceptionTranslator()))
        return jooqCfg
    }

}

class ExceptionTranslator : DefaultExecuteListener() {

    val logger = JooqLogger.getLogger(ExceptionTranslator::class.java)

    override fun start(ctx: ExecuteContext) {
        ctx.data("time", System.nanoTime())
    }

    override fun end(ctx: ExecuteContext) {
        val time = ctx.data("time") as Long
        logger.debug("Execution time : " + (System.nanoTime() - time).toDouble() / 1000.0 / 1000.0 + "ms. Query : " + ctx.sql())
    }
}