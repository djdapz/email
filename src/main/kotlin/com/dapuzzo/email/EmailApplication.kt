package com.dapuzzo.email

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.SpringApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableWebMvc
open class EmailApplication

fun main(args: Array<String>) {
    SpringApplication.run(EmailApplication::class.java, *args)
}
