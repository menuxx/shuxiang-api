package com.menuxx

import com.menuxx.common.db.ChannelItemRecordDb
import com.menuxx.miaosha.ChannelResumeRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication(
        scanBasePackages = [
            "com.menuxx.common.*",
            "com.menuxx.apiserver.*",
            "com.menuxx.miaosha.*"
        ]
)
@ComponentScan
@EnableTransactionManagement
class Application {

    @Bean
    @Autowired
    fun launch(channelItemRecordDb: ChannelItemRecordDb): CommandLineRunner {
        return ChannelResumeRunner(channelItemRecordDb)
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}