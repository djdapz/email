package com.dapuzzo.email.sendgrid

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class MailgunConfig(
    @Value("\${dapuzzo.mail.mailgun.username}") val apiUsername: String,
    @Value("\${dapuzzo.mail.mailgun.password}") val apiPassword: String,
    @Value("\${dapuzzo.mail.mailgun.url}") val url: String
)