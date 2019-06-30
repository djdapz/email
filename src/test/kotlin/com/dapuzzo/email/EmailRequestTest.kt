package com.dapuzzo.email

import com.github.javafaker.Faker
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Test
import java.lang.RuntimeException


class EmailRequestTest {

    @Test
    fun `should tell me email with dashes is valid`() {
        assertThat("Joe-guy@email.com".isValidEmail).isTrue()
    }

    @Test
    fun `should tell me valid email is ok`() {
        assertThat("squirrel@potato.org".isValidEmail).isTrue()
    }

    @Test
    fun `should tell me email with space is not valid`() {
        assertThat("Joe guy@email.com".isValidEmail).isFalse()
    }

    @Test
    fun `should tell me email with capital letters is valid`() {
        assertThat("NarcissaMalfoy@Dare-Dicki.com".isValidEmail).isTrue()
    }


    @Test
    fun `should allow valid email address`() {
        randomEmailRequest(email = "squirrel@potato.org")
    }

    @Test
    fun `should alow valid email address 2`() {
        randomEmailRequest("jimbob@jimbob.app")
    }


    @Test
    fun `should ensure that the desination is valid`() {
        assertThrows<RuntimeException>("Invalid Email Address(es) Found: [invalid@invalid]") {
            EmailRequest(
                name = "hi",
                from = "squirrel@potato.org",
                to = listOf("squirrel@potato.org", "invalid@invalid"),
                message = "HI"
            )
        }
    }

    @Test
    fun `should reject malformed email address`() {
        assertThrows<RuntimeException>("Invalid Email Address(es) Found: [bad address]") {
            EmailRequest(
                name = "hi",
                from = "bad address",
                to = listOf("squirrel@potato.org"),
                message = "HI"
            )
        }
    }

    companion object {
        private val faker = Faker()
        fun randomEmailRequest(
            email: String = randomEmail()
        ): EmailRequest = EmailRequest(
            name = faker.esports().event(),
            from = email,
            to = listOf(randomEmail(), randomEmail()),
            message = faker.harryPotter().quote()
        )

        private fun String.removeNonEmailChars() = this.replace(" ", "")
            .replace(",", "")
            .replace(")", "")
            .replace("(", "")
            .replace("'", "")

        private fun randomEmail() =
            "${faker.harryPotter().character().removeNonEmailChars()}@${faker.company().name().removeNonEmailChars()}.com"
    }
}

inline fun <reified U : Throwable> assertThrows(message: String?, function: () -> Any) {
    try {
        function()
        fail<String>("Your function invocation did not throw an exception")
    } catch (e: Throwable) {
        if (e is U) {
            assertThat(e.message).isEqualTo(message)
        } else {
            throw e
        }
    }


}