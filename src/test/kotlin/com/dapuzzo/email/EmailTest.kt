package com.dapuzzo.email

import com.nhaarman.mockitokotlin2.verify
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.math.BigInteger


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
@ActiveProfiles("test")
@Sql(scripts = ["classpath:clean.sql"])
class EmailTest {

    @LocalServerPort
    lateinit var port: BigInteger

    @MockBean
    lateinit var mailSender: JavaMailSender

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun shouldUsePassedEmailToDetermineLocation() {
        val argument = ArgumentCaptor.forClass(SimpleMailMessage::class.java)
        val to = listOf("luke@luke.luke", "devon@devon.devon")

        sendEmail(to = to)


        verify(mailSender).send(argument.capture())

        val sentMessage = argument.value

        assertThat(sentMessage.to).isEqualTo(to.toTypedArray())
        assertThat(sentMessage.subject).isEqualTo("LUKE D'APUZZO Website Contact")
    }


    @Test
    fun `should save email sent in db`() {

        val dude = randomEmail()

        assertThat(howManyEmailsFrom(dude)).isEqualTo(0)


        sendEmail(
            from = dude
        )

        assertThat(howManyEmailsFrom(dude)).isEqualTo(1)
    }

    fun howManyEmailsFrom(dude: String): Int =
        jdbcTemplate.queryForObject("SELECT count(*) FROM sent_emails WHERE request ->> 'from' ='$dude'") {it, _ ->
            it.getInt("count")
        }

    private fun sendEmail(
        name: String = "joe",
        message: String = "hey bro",
        from: String = "email@gmail.com",
        to: List<String> = listOf("luke@luke.luke")
    ) {
        val toList = to.joinToString(", ") { "\"$it\"" }
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(
//                language=json
                """
                    {
                        "name": "$name",
                        "message": "$message",
                        "from": "$from",
                        "to": [$toList]
                    }
                """.trimIndent()
            )
            .`when`()
            .post("http://localhost:$port/email")
            .then()
            .log().all()
            .statusCode(200)
    }


}
