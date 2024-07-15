package com.gawkgawk.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Configures serialization for the application.
 * This function installs the ContentNegotiation plugin and configures it to use JSON serialization.
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}
