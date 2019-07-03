package com.dapuzzo.email.sendgrid

import com.dapuzzo.email.app.EmailConfig
import com.dapuzzo.email.app.EmailRequest
import com.dapuzzo.email.app.EmailSender
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate


@Component
class MailgunSender(
    val mailgunConfig: MailgunConfig,
    val restTemplate: RestTemplate,
    val emailConfig: EmailConfig
) : EmailSender {

    private val headers
        get() =
            HttpHeaders().apply {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                setBasicAuth(mailgunConfig.apiUsername, mailgunConfig.apiPassword)
            }


    private val EmailRequest.asParms: LinkedMultiValueMap<String, String>
        get() =
            LinkedMultiValueMap<String, String>().apply {
                add("from", emailConfig.from)
                add("subject", emailConfig.subject)
                add("text", this@asParms.formattedMessage)
                this@asParms.to.forEach { to -> add("to", to) }
            }

    override fun send(email: EmailRequest) {
        restTemplate.postForEntity(
            mailgunConfig.url,
            HttpEntity(email.asParms, headers),
            String::class.java
        )
    }
}