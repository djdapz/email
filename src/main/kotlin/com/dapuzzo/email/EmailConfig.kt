package com.dapuzzo.email

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class EmailConfig {

    @Bean
    fun javaMailSender(
        @Value("\${spring.mail.username}") emailAddress: String,
        @Value("\${spring.mail.password}") password: String,
        @Value("\${spring.mail.host}") host: String,
        @Value("\${spring.mail.port}") port: Int
    ): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = host
        mailSender.port = port

        mailSender.username = emailAddress
        mailSender.password = password

        val props = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        return mailSender
    }
}
