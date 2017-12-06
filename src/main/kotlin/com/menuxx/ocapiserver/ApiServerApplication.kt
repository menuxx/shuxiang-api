package com.menuxx.ocapiserver

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
class ApiServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(ApiServerApplication::class.java, *args)
}