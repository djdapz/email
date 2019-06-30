package com.dapuzzo.email

import com.nhaarman.mockitokotlin2.verify
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.junit4.SpringRunner
import org.mockito.ArgumentCaptor
import org.springframework.mail.SimpleMailMessage
import org.springframework.test.context.ActiveProfiles
import org.springframework.boot.web.server.LocalServerPort
import java.math.BigInteger


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
@ActiveProfiles("test")
class EmailTest {

    @LocalServerPort
    lateinit var port: BigInteger

    @MockBean
    lateinit var mailSender: JavaMailSender

    @Test
    fun shouldUsePassedEmailToDetermineLocation() {
        val argument = ArgumentCaptor.forClass(SimpleMailMessage::class.java)

        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(
                //language=json
                """
                {
                    "name": "dude",
                    "message": "great stuff",
                    "from": "jimbob@jimbob.bob",
                    "to": ["luke@luke.luke", "devon@devon.devon"]
                }
            """.trimIndent()
            )
            .`when`()
            .post("http://localhost:$port/email")
            .then()
            .log().all()
            .statusCode(200)


        verify(mailSender).send(argument.capture())

        val sentMessage = argument.value

        assertThat(sentMessage.to).isEqualTo(listOf("luke@luke.luke", "devon@devon.devon").toTypedArray())
        assertThat(sentMessage.subject).isEqualTo("LUKE D'APUZZO Website Contact")
    }


}
