package com.dapuzzo.email

import com.dapuzzo.email.EmailRequestTest.Companion.randomEmailRequest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender

/**
 * Created by devondapuzzo on 10/24/17.
 */
class EmailServiceTest {

    private val javaMailSender = mock<JavaMailSender>()
    private val subject = EmailService(javaMailSender)

    @Test
    fun `should call JavaMailSender once `() {
        subject.sendEmail(randomEmailRequest())
        verify(javaMailSender, times(1)).send(any<SimpleMailMessage>())
    }
}