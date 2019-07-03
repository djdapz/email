package com.dapuzzo.email.app

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
        val result = emailService.sendEmail(emailRequest)
        if (result.isFailure) return ResponseEntity.status(500).body("Kaboom")
        return ResponseEntity.ok("Great Success")
    }
}
