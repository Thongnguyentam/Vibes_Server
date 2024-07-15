package com.gawkgawk.repository.profile

import com.gawkgawk.model.ProfileResponse
import com.gawkgawk.model.UpdateUserParams
import com.gawkgawk.util.Response

interface ProfileRepository {
    suspend fun getUserById(userId: Long, currentUserId: Long): Response<ProfileResponse>

    suspend fun updateUser(updateUserParams: UpdateUserParams): Response<ProfileResponse>
}