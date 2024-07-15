package com.gawkgawk.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.gawkgawk.dao.user.UserDao
import com.gawkgawk.model.AuthResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kotlin.concurrent.getOrSet

private const val CLAIM = "email"
private val jwtAudience = System.getenv("jwt.audience")
private val jwtDomain = System.getenv("jwt.domain")
private val jwtSecret = System.getenv("jwt.secret")
private val errorReason = ThreadLocal<String>()
// authorization is what do you have access to
// jwt is for 2 reasons:
//  + we don't need to ask user each time that the client need to make the request to provide an email and password
//    instead we're going to generate a token which will contain some user information
//  + since these tokens are going to be signed using our JWT secret key that we are provide using certain algorithm
//    you can be sure that the token cannot be tampered (cannot be modified)

/**
 * Configures the security settings for the application using JWT authentication.
 * This function sets up the JWT authentication provider and defines how tokens are verified and validated.
 */
fun Application.configureSecurity() {
    // Please read the jwt property from the config file if you are using EngineMain
    val userDao by inject<UserDao>()
    val logger = LoggerFactory.getLogger(this::class.java)
    authentication {
        jwt {
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                val email = credential.payload.getClaim(CLAIM).asString()
                logger.info("Validating token: email=$email")
                if (email != null){
                    val userExists = userDao.findByEmail(email = email) != null
                    val isValidAudience = credential.payload.audience.contains(jwtAudience)
                    logger.info("userExists=$userExists, isValidAudience=$isValidAudience")
                    if (userExists && isValidAudience) {
                        JWTPrincipal(payload = credential.payload)
                    }
                    else{
                        errorReason.set("User does not exist")
                        return@validate null
                    }
                } else{
                    // if the token does contain the email
                    // return null which means the token cannot be validated or the token is invalid
                    errorReason.set("Email Missing")
                    return@validate null
                }
            }

            // this case the token is invalid, because the above it is already check that
            // the token was modified (by the verifier) or if the token doesnt contain claim:
            challenge{_, _  ->
                val errorMessage = errorReason.getOrSet { "Token is not valid or has expired" }
                call.respond(

                    status = HttpStatusCode.Unauthorized,
                    message = AuthResponse(
                        errorMessage = errorMessage
                    )
                )
                errorReason.remove()
            }
        }
    }
}

/**
 * Generates a JWT token for the given email.
 * @param email The email to include in the token's claims.
 * @return A signed JWT token.
 */
fun generateToken(email:String): String{
    return JWT.create()
        .withAudience(jwtAudience)
        .withIssuer(jwtDomain)
        .withClaim(CLAIM, email)
        // .withExpiresAt()
        .sign(Algorithm.HMAC256(jwtSecret))
}