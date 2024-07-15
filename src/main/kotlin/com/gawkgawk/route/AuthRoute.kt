package com.gawkgawk.route

import com.gawkgawk.model.AuthResponse
import com.gawkgawk.model.SignInParams
import com.gawkgawk.model.SignUpParams
import com.gawkgawk.repository.auth.AuthRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

/**
 * Sets up routes for authentication in a Ktor server.
 * This function defines the routes for user sign-up and sign-in,
 * , handling HTTP POST requests for these routes.
 */
fun Routing.authRouting(){
    // inject an instance of UserRepository into the function.
    // This allows the function to access the repository without needing to create it explicitly.
    val repository by inject<AuthRepository>()

    // Define a route for signup
    route(path = "/signup"){
        // Handle POST requests to the /signup route,
        // an HTTP method used to send data to a server to create or update a resource.
        post {
            // "Call" represents a single request-response cycle in the Ktor application.
            // The ApplicationCall object provides access to the incoming request and the outgoing response,
            // allowing you to handle and respond to HTTP requests within routing blocks.


            // receive and deserialize the request body into an instance of SignUpParams.
            // If the request body is not present or cannot be deserialized, it returns null.
            val params = call.receiveNullable<SignUpParams>()
            if (params == null){
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = AuthResponse(
                        errorMessage = "Invalid Credentials"
                    )
                )
                // Exits the POST request handler if params is null.
                return@post
            }
            // params is not null
            // It delegates the sign-up process to the UserRepository
            val result = repository.signUp(params = params) // result is the response of server
            // Responds to the client with the status and message from the sign-up result.
            call.respond(
                status = result.code,
                message = result.data
            )
        }
    }

    route(path = "/login"){
        post {
            val params = call.receiveNullable<SignInParams>()

            if(params == null){
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = AuthResponse(
                        errorMessage = "Invalid credentials!"
                    )
                )
                return@post
            }

            val result = repository.signIn(params = params)
            call.respond(
                status = result.code,
                message = result.data
            )
        }
    }
}