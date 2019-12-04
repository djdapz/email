package com.dapuzzo.email.app

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@EnableWebMvc
class WebConfig(@Value("\${dapuzzo.allowed-origins}") val allowedOrigins: List<String>) : WebMvcConfigurer {


    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(*allowedOrigins.toTypedArray())
    }
}