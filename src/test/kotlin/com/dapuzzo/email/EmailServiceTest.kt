package com.dapuzzo.email

import com.dapuzzo.email.app.EmailRepository
import com.dapuzzo.email.app.EmailSender
import com.dapuzzo.email.app.EmailService
import com.nhaarman.mockitokotlin2.*
import org.junit.Test
import java.lang.RuntimeException

/**
 * Created by devondapuzzo on 10/24/17.
 */
class EmailServiceTest {

    private val emailClient = mock<EmailSender>()
    private val emailRepository = mock<EmailRepository>()
    private val subject = EmailService(emailClient, emailRepository)

    @Test
    fun `should use sendgrid`(){
        val emailRequest = randomEmailRequest()
        subject.sendEmail(emailRequest)

        verify(emailClient).send(emailRequest)
    }


    @Test
    fun `should call JavaMailSender once `() {
        subject.sendEmail(randomEmailRequest())
        verify(emailClient, times(1)).send(any())
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
        whenever(emailClient.send(any())).doThrow(RuntimeException("muahahaha"))

        subject.sendEmail(request)

        verify(emailRepository).saveFailure(request)
    }

}