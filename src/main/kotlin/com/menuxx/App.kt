package com.menuxx

import com.menuxx.common.prop.AppProps
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.transaction.annotation.EnableTransactionManagement

@EnableCaching
@SpringBootApplication(
        scanBasePackages = [
            "com.menuxx.common.*",
            "com.menuxx.apiserver.*",
            "com.menuxx.miaosha.*"
        ]
)
@ComponentScan
@EnableTransactionManagement
@EnableConfigurationProperties(AppProps::class)
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}