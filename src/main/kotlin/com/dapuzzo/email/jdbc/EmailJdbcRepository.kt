package com.dapuzzo.email.jdbc

import com.dapuzzo.email.app.EmailRepository
import com.dapuzzo.email.app.EmailRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import org.postgresql.util.PGobject


@Component
class EmailJdbcRepository(jdbcTemplate: JdbcTemplate, val objectMapper: ObjectMapper) :
    EmailRepository {
    val jdbcTemplate = NamedParameterJdbcTemplate(jdbcTemplate)

    override fun saveSuccess(request: EmailRequest): Unit = save(request, true)
    override fun saveFailure(request: EmailRequest): Unit = save(request, false)

    private fun save(request: EmailRequest, success: Boolean): Unit =
        PGobject().apply {
            type = "json"
            value = objectMapper.writeValueAsString(request)
        }.run {
            jdbcTemplate.update(
                "INSERT INTO sent_emails (request, is_success) values (:request, :success)",
                MapSqlParameterSource()
                    .addValue("request", this)
                    .addValue("success", success)
            )
        }

}