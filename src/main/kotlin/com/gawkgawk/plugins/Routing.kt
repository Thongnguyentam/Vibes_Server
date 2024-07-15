package com.gawkgawk.plugins

import com.gawkgawk.route.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
/**
 * Configures the routing for the application.
 * This function sets up the routing configuration by invoking the authRouting function.
 */
fun Application.configureRouting() {
    // defining the routes within the routing block.
    routing {
        authRouting() //  contains the authentication-related routes.
        followsRouting()
        postRouting()
        profileRouting()
        postCommentsRouting()
        postLikesRouting()
        staticResources("/","static")
    }
}
