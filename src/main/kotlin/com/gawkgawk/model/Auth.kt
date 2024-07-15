package com.gawkgawk.model

import kotlinx.serialization.Serializable

/**
 * A parameter that our users will pass to register a new account.
 * @property name The name of the user.
 * @property email The email of the user.
 * @property password The password of the user.
 */
@Serializable
data class SignUpParams(
    val name: String,
    val email: String,
        val password: String
)

/**
 * A parameter that our users will pass to sign in to their account.
 * @property email The email of the user.
 * @property password The password of the user.
 */
@Serializable
data class SignInParams(
    val email: String,
    val password: String
)


/**
 * The response that we are going to return to the user after authentication.
 * @property data The authentication response data if the authentication was successful.
 * @property errorMessage The error message if the authentication failed.
 */
@Serializable
data class AuthResponse(
    val data: AuthResponseData? = null,
    val errorMessage: String? = null
)

/**
 * The response data that we are going to return to the user after successful authentication.
 * @property id The ID of the user.
 * @property name The name of the user.
 * @property bio The bio of the user.
 * @property avatar The avatar URL of the user, if available.
 * @property token The authentication token for the user.
 * @property followersCount The number of followers the user has.
 * @property followingCount The number of users the user is following.
 */
@Serializable
data class AuthResponseData(
    val id: Long,
    val name: String,
    val bio: String,
    val avatar: String? = null,
    val token: String,
    val followersCount: Int = 0,
    val followingCount: Int = 0

)