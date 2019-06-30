package com.dapuzzo.email

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component


@Component
class EmailService(
    val emailSender: JavaMailSender,
    val emailRepository: EmailRepository
) {
    fun sendEmail(emailRequest: EmailRequest) {

        emailSender.runCatching {
            send(SimpleMailMessage().apply {
                setTo(*emailRequest.to.toTypedArray())
                subject = "LUKE D'APUZZO Website Contact"
                text = emailRequest.formattedMessage
            })
        }
            .onSuccess { emailRepository.saveSuccess(emailRequest) }
            .onFailure { emailRepository.saveFailure(emailRequest) }
    }

}
