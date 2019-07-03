package com.dapuzzo.email.app

interface EmailSender {
    fun send(email: EmailRequest)
}