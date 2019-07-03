package com.dapuzzo.email.app

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
import org.junit.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

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
    @Ignore("Ignoring to see if a kotlin update fixes this, Integration tests cover this case, but i'd like a unit test")
    fun `should return a response entity with 500 when an email fails to send`() {

        val result: Result<Unit> = runCatching {
            throw Throwable(":(")
        }

        assertThat(result.isFailure).isTrue()

        whenever(emailService.sendEmail(any())).doReturn(result)

        val x = subject.sendEmail(
            EmailRestRequest(
                name = "jim",
                message = "hello",
                from = "goo@goo.goo",
                to = listOf("hi@hi.hi")
            )
        )

        assertThat(x.statusCode).isEqualTo(500)
    }


}