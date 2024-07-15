package com.gawkgawk

import com.gawkgawk.dao.DatabaseFactory
import com.gawkgawk.di.configureDI
import com.gawkgawk.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

/**
 * The main entry point for the Ktor application.
 * This function starts the Ktor server using the Netty engine.
 */
fun main(args: Array<String>) {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    )
        .start(wait = true)
}

/**
 * The main application module.
 * This function initializes the database, configures serialization, dependency injection, security, and routing.
 */
fun Application.module() {
    DatabaseFactory.init() //set up the database connection and initialize the schema.
    configureSerialization()
    configureDI()
    configureSecurity()
    configureRouting()
}
