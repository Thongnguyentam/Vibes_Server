package com.gawkgawk.repository.auth

import com.gawkgawk.model.AuthResponse
import com.gawkgawk.model.SignInParams
import com.gawkgawk.model.SignUpParams
import com.gawkgawk.util.Response
/**
 * Repository interface for user-related operations.
 */
interface AuthRepository {
    /**
     * Signs up a new user with the provided parameters.
     * @param params The parameters required for user sign-up.
     * @return A Response containing the AuthResponse data.
     */
    suspend fun signUp(params: SignUpParams): Response<AuthResponse>

    /**
     * Signs in a user with the provided parameters.
     * @param params The parameters required for user sign-in.
     * @return A Response containing the AuthResponse data.
     */
    suspend fun signIn(params: SignInParams): Response<AuthResponse>
}