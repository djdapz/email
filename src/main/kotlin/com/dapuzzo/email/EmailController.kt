package com.dapuzzo.email

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EmailController(
    val emailService: EmailService
) {


    @PostMapping("/email")
    fun sendEmail(@RequestBody emailRequest: EmailRequest): ResponseEntity<*> {
        emailService.sendEmail(emailRequest)

        return ResponseEntity.ok("Great Success")
    }
}
