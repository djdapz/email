package com.dapuzzo.email

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.sql.Timestamp
import java.time.LocalDateTime

@SpringBootTest
@RunWith(SpringRunner::class)
@ActiveProfiles("test")
@Sql(scripts = ["classpath:clean.sql"])
internal class EmailJdbcRepositoryTest {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var emailJdbcRepository: EmailJdbcRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `should save a successful email request in the DB`() {

        val request = EmailRequest(
            name = "jimbo jones",
            to = listOf("bil@bil.bil"),
            from = "joe@joe.joe",
            message = "nice stuff bro"
        )

        emailJdbcRepository.saveSuccess(request)

        val jsonRequest =
            jdbcTemplate.queryForObject("""SELECT * FROM sent_emails where request ->> 'from' ='joe@joe.joe'""")
            { rs, _ -> rs.getString("request") }

        assertThat(jsonRequest).isEqualTo(objectMapper.writeValueAsString(request))
    }

    @Test
    fun `should say that  email was succesful`() {
        val request = EmailRequest(
            name = "jimbo jones",
            to = listOf("bil@bil.bil"),
            from = "joe@joe.joe",
            message = "nice stuff bro"
        )

        emailJdbcRepository.saveSuccess(request)

        val isSuccess =
            jdbcTemplate.queryForObject("""SELECT * FROM sent_emails where request ->> 'from' ='joe@joe.joe'""")
            { rs, _ -> rs.getBoolean("is_success") }

        assertThat(isSuccess).isTrue()
    }

    @Test
    fun `should say that  email was not succesful`() {
        val request = EmailRequest(
            name = "jimbo jones",
            to = listOf("bil@bil.bil"),
            from = "joe@joe.joe",
            message = "nice stuff bro"
        )

        emailJdbcRepository.saveFailure(request)

        val isSuccess =
            jdbcTemplate.queryForObject("""SELECT * FROM sent_emails where request ->> 'from' ='joe@joe.joe'""")
            { rs, _ -> rs.getBoolean("is_success") }

        assertThat(isSuccess).isFalse()
    }

    @Test
    fun `should save a failure email request in the DB`() {
        val request = randomEmailRequest()

        emailJdbcRepository.saveFailure(request)

        val jsonRequest =
            jdbcTemplate.queryForObject("""SELECT * FROM sent_emails where request ->> 'from' ='${request.from}'""")
            { rs, _ -> rs.getString("request") }

        assertThat(jsonRequest).isEqualTo(objectMapper.writeValueAsString(request))
    }

    @Test
    fun `should save the datetime when it's created`() {
        val before = LocalDateTime.now()

        val request = randomEmailRequest()
        emailJdbcRepository.saveFailure(request)


        val savedTime =
            jdbcTemplate.queryForObject("""SELECT * FROM sent_emails where request ->> 'from' ='${request.from}'""")
            { rs, _ -> rs.getTimestamp("date").toLocalDateTime() }

        val after = LocalDateTime.now()

        assertThat(savedTime).isAfter(before)
        assertThat(savedTime).isBefore(after)

    }


}
