package com.menuxx

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement

@EnableCaching
@EnableScheduling
@SpringBootApplication(
        scanBasePackages = [
            "com.menuxx.common.*",
            "com.menuxx.apiserver.*",
            "com.menuxx.miaosha.*"
        ]
)
@ComponentScan
@EnableTransactionManagement
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}