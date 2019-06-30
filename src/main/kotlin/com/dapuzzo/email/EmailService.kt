package com.dapuzzo.email

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component


@Component
class EmailService(
    val emailSender: JavaMailSender
) {
    fun sendEmail(emailRequest: EmailRequest) =
        emailSender.send(
            SimpleMailMessage().apply {
                setTo(*emailRequest.to.toTypedArray())
                subject = "LUKE D'APUZZO Website Contact"
                text = emailRequest.formattedMessage
            })
}
