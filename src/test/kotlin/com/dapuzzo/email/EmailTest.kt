package com.dapuzzo.email

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Captor
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.math.BigInteger


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
@ActiveProfiles("test")
@Sql(scripts = ["classpath:clean.sql"])
class EmailTest {

    @LocalServerPort
    lateinit var port: BigInteger

    @MockBean
    lateinit var restTemplate: RestTemplate

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Captor
    lateinit var captor: ArgumentCaptor<HttpEntity<MultiValueMap<String, String>>>

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun shouldUsePassedEmailToDetermineLocation() {
        val to = listOf("luke@luke.luke", "devon@devon.devon")

        sendEmail(to = to)


        verify(restTemplate).postForEntity(any<String>(), captor.capture(), any<Class<Any>>())

        val sentPeople: List<String> = captor.value.body["to"]!!
        val sentSubject: String = captor.value.body["subject"]!![0]

        assertThat(sentPeople).containsExactlyElementsOf(to)
        assertThat(sentSubject).isEqualTo("woohoo stuff")
    }

    @Test
    fun shouldReturnA500WhenEmailIsNotSent() {
        whenever(restTemplate.postForEntity(anyString(), any(), any<Class<Any>>())).doThrow(RuntimeException("Kaboom"))
        sendEmail(expectedCode = 500)
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
        jdbcTemplate.queryForObject("SELECT count(*) FROM sent_emails WHERE request ->> 'from' ='$dude'") { it, _ ->
            it.getInt("count")
        }

    private fun sendEmail(
        name: String = "joe",
        message: String = "hey bro",
        from: String = "email@gmail.com",
        to: List<String> = listOf("luke@luke.luke"),
        expectedCode: Int = 200
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
            .statusCode(expectedCode)
    }


}
