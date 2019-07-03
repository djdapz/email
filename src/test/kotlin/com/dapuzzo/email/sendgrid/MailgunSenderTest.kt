package com.dapuzzo.email.sendgrid

import com.dapuzzo.email.app.EmailConfig
import com.dapuzzo.email.randomEmailRequest
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.springframework.http.HttpEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.mockito.MockitoAnnotations
import org.junit.Before
import org.mockito.ArgumentMatchers.anyString
import java.util.*


class MailgunSenderTest {

    private val restTemplate = mock<RestTemplate>()
    private val apiPassword = Faker().random().nextLong().toString()
    private val apiUsername = Faker().random().nextLong().toString()
    private val subjectLine = Faker().rickAndMorty().quote()
    private val from = Faker().gameOfThrones().quote()
    private val url = Faker().internet().url()

    private val subejct = MailgunSender(
        MailgunConfig(apiUsername, apiPassword, url),
        restTemplate,
        EmailConfig(subjectLine, from)
    )

    @Captor
    lateinit var captor: ArgumentCaptor<HttpEntity<MultiValueMap<String, String>>>

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `should call rest template with injected url`() {

        subejct.send(randomEmailRequest())

        verify(restTemplate).postForEntity(eq(url), any(), any<Class<Any>>())

    }

    @Test
    fun `should call rest template with injected api key`() {
        val raw = "$apiUsername:$apiPassword"
        val encodedBytes = Base64.getEncoder().encode(raw.toByteArray())
        val encodedCredentials = String(encodedBytes)

        subejct.send(randomEmailRequest())

        verify(restTemplate).postForEntity(anyString(), captor.capture(), any<Class<Any>>())

        val parameters = captor.value
        val capturedHeader: String = parameters.headers["Authorization"]!![0]

        assertThat(capturedHeader).isEqualTo("Basic $encodedCredentials")

    }


    @Test
    fun `should send passed email`() {
        val email = randomEmailRequest()
        subejct.send(email)

        verify(restTemplate).postForEntity(anyString(), captor.capture(), any<Class<Any>>())

        val parameters = captor.value
        val capturedText: String = parameters.body["text"]!![0]

        assertThat(capturedText).isEqualTo(email.formattedMessage)

    }


    @Test
    fun `should use injected subject`() {
        val email = randomEmailRequest()
        subejct.send(email)

        verify(restTemplate).postForEntity(anyString(), captor.capture(), any<Class<Any>>())

        val capturedSubject: String = captor.value.body["subject"]!![0]

        assertThat(capturedSubject).isEqualTo(subjectLine)
    }

    @Test
    fun `should use injected from title`() {
        subejct.send(randomEmailRequest())

        verify(restTemplate).postForEntity(anyString(), captor.capture(), any<Class<Any>>())

        val capturedFrom: String = captor.value.body["from"]!![0]

        assertThat(capturedFrom).isEqualTo(from)
    }

    @Test
    fun `should send to everyone on the list`() {
        val email = randomEmailRequest()
        subejct.send(email)

        verify(restTemplate).postForEntity(anyString(), captor.capture(), any<Class<Any>>())

        val capturedTo = captor.value.body["to"]!!

        assertThat(capturedTo).containsExactlyElementsOf(email.to)
    }
}