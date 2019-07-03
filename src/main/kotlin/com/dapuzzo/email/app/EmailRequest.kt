package com.dapuzzo.email.app

val REGEX = Regex("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})\$")

val String.isValidEmail
    get() = this.matches(REGEX)

class EmailRequest(
    name: String,
    val to: List<String>,
    val from: String,
    message: String
) {

    init {
        val badAddresses = listOf(*to.toTypedArray(), from).filter { thing -> !thing.isValidEmail }
        if (badAddresses.isNotEmpty()) throw RuntimeException("Invalid Email Address(es) Found: $badAddresses")
    }


    internal val formattedMessage = """
            FROM: $name | $from
            -----------------------------------------------------
            MESSAGE: $message
            """.trimIndent()

}
