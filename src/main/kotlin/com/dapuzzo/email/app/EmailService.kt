package com.dapuzzo.email.app

import org.springframework.stereotype.Component


@Component
class EmailService(
    val emailSender: EmailSender,
    val emailRepository: EmailRepository
) {
    fun sendEmail(emailRequest: EmailRequest): Result<Unit> = emailSender
        .runCatching { emailSender.send(emailRequest) }
        .onSuccess { emailRepository.saveSuccess(emailRequest) }
        .onFailure { e ->
            e.printStackTrace()
            emailRepository.saveFailure(emailRequest)
        }
}
