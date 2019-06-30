package com.dapuzzo.email

interface EmailRepository {
    fun saveSuccess(request: EmailRequest)
    fun saveFailure(request: EmailRequest)

}
