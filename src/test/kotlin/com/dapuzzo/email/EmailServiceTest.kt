package com.dapuzzo.email

import com.nhaarman.mockitokotlin2.*
import org.junit.Test
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import java.lang.RuntimeException

/**
 * Created by devondapuzzo on 10/24/17.
 */
class EmailServiceTest {

    private val javaMailSender = mock<JavaMailSender>()
    private val emailRepository = mock<EmailRepository>()
    private val subject = EmailService(javaMailSender, emailRepository)

    @Test
    fun `should call JavaMailSender once `() {
        subject.sendEmail(randomEmailRequest())
        verify(javaMailSender, times(1)).send(any<SimpleMailMessage>())
    }

    @Test
    fun `should save the email request with success after sending`() {
        val request = randomEmailRequest()
        subject.sendEmail(request)
        verify(emailRepository).saveSuccess(request)
    }

    @Test
    fun `should save the email request with failure if the email request doesnt work`() {
        val request = randomEmailRequest()
        whenever(javaMailSender.send(any<SimpleMailMessage>())).doThrow(RuntimeException("muahahaha"))

        subject.sendEmail(request)

        verify(emailRepository).saveFailure(request)
    }

}