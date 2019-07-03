package com.dapuzzo.email.app

interface EmailRepository {
    fun saveSuccess(request: EmailRequest)
    fun saveFailure(request: EmailRequest)

}
