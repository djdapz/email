package com.dapuzzo.email.app

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class AppConfig {
    @Bean
    fun getRestTemplate(): RestTemplate = RestTemplate()

    @Bean
    fun getEmailConfg(
        @Value("\${dapuzzo.mail.subject}") subject: String,
        @Value("\${dapuzzo.mail.from}") from: String
    ): EmailConfig = EmailConfig(subject, from)
}

class EmailConfig(val subject: String, val from: String)
