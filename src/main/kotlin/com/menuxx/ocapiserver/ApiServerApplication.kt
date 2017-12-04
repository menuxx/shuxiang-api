package com.menuxx.ocapiserver

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ApiServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(ApiServerApplication::class.java, *args)
}