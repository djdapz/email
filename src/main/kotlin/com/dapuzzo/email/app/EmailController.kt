package com.dapuzzo.email.app

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class EmailRestRequest(val to: List<String>, val from: String, val message: String, val name: String) {
    fun toEmailRequest() = EmailRequest(name, to, from, message)
}

@RestController
class EmailController(
    val emailService: EmailService
) {


    @PostMapping("/email")
    fun sendEmail(@RequestBody request: EmailRestRequest): ResponseEntity<*> {

        return request
            .runCatching {
                toEmailRequest()
            }.fold(
                { sendEmail(it) },
                { ResponseEntity.status(400).body("BAD BODY") }
            )
    }

    private fun sendEmail(it: EmailRequest): ResponseEntity<String> =
        emailService.sendEmail(it)
            .fold(
                { ResponseEntity.ok("Great Success") },
                { ResponseEntity.status(500).body("Kaboom") }
            )
}
