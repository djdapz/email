package com.dapuzzo.email.app

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

internal class EmailControllerTest {

    val emailService = mock<EmailService>()
    val subject = EmailController(emailService)
    val mockMvc = MockMvcBuilders.standaloneSetup(subject).build();

    @Test
    fun `should return a response entity with 400 when an invalid email address is sent`() {
        mockMvc.perform(
            post("/email").content(
                """
            {
                "name": "jim",
                "message": "hello",
                "from": "BADEMAIL",
                "to": ["hi@hi.hi"]
            }
        """.trimIndent()
            )
                .contentType("application/json")
        ).andExpect(status().`is`(400))

    }

    @Test
    fun `should return a response entity with 500 when an email fails to send`() {

        whenever(emailService.sendEmail(any())).doThrow(RuntimeException("it failed"))
        mockMvc.perform(
            post("/email").content(
                """
            {
                "name": "jim",
                "message": "hello",
                "from": "goo@goo.goo",
                "to": ["hi@hi.hi"]
            }
        """.trimIndent()
            )
                .contentType("application/json")
        ).andExpect(status().`is`(500))

    }


}