package com.gawkgawk.repository.auth

import com.gawkgawk.dao.user.UserDao
import com.gawkgawk.model.AuthResponse
import com.gawkgawk.model.AuthResponseData
import com.gawkgawk.model.SignInParams
import com.gawkgawk.model.SignUpParams
import com.gawkgawk.plugins.generateToken
import com.gawkgawk.security.hashPassword
import com.gawkgawk.util.Response
import io.ktor.http.*
/**
 * Implementation of the UserRepository interface.
 * Handles the business logic for user sign-up and sign-in.
 */
class AuthRepositoryImpl (
    private val userDao: UserDao
): AuthRepository {

    /**
     * Signs up a new user with the provided parameters.
     * @param params The parameters required for user sign-up.
     * @return A Response containing the AuthResponse data.
     */
    override suspend fun signUp(params: SignUpParams): Response<AuthResponse> {
        return if (userAlreadyExist(params.email)){
            Response.Error(
                code = HttpStatusCode.Conflict,
                data = AuthResponse(
                    errorMessage = "A user with this email already exists"
                ))
        }
        else{
            val insertedUser = userDao.insert(params)
            if (insertedUser == null){
                Response.Error(
                    code = HttpStatusCode.InternalServerError,
                    data = AuthResponse(
                        errorMessage = "Ooops, sorry we could not register the user, try later !"
                    )
                )
            }
            else{
                Response.Success(
                    data = AuthResponse(
                        data = AuthResponseData(
                            id = insertedUser.id,
                            name = insertedUser.name,
                            bio = insertedUser.bio,
                            token = generateToken(params.email)

                        )
                    )
                )
            }
        }
    }

    /**
     * Signs in a user with the provided parameters.
     * @param params The parameters required for user sign-in.
     * @return A Response containing the AuthResponse data.
     */

    override suspend fun signIn(params: SignInParams): Response<AuthResponse> {
        val user = userDao.findByEmail(params.email)

        return if (user == null){
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = AuthResponse(
                    errorMessage = "Invalid credentials, no user with this email!"
                )
            )
        } else{
            val hashedPassword = hashPassword(params.password)
            if (user.password == hashedPassword){
                Response.Success(
                    data = AuthResponse(
                        data = AuthResponseData(
                            id = user.id,
                            name = user.name,
                            bio = user.bio,
                            token = generateToken(params.email),
                            followersCount = user.followersCount,
                            followingCount = user.followingCount
                        )
                    )
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.Forbidden,
                    data = AuthResponse(
                        errorMessage = "Invalid credentials, wrong passwords!"
                    )
                )
            }
        }
    }

    /**
     * Checks if a user with the given email already exists.
     * @param email The email to check.
     * @return True if the user already exists, otherwise false.
     */
    private suspend fun userAlreadyExist(email: String): Boolean{
        return userDao.findByEmail(email) != null
    }
}