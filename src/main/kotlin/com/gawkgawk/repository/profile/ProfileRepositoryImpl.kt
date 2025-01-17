package com.gawkgawk.repository.profile

import com.gawkgawk.dao.follows.FollowsDao
import com.gawkgawk.dao.user.UserDao
import com.gawkgawk.dao.user.UserRow
import com.gawkgawk.model.Profile
import com.gawkgawk.model.ProfileResponse
import com.gawkgawk.model.UpdateUserParams
import com.gawkgawk.util.Response
import io.ktor.http.*

class ProfileRepositoryImpl(
    private val userDao: UserDao,
    private val followsDao: FollowsDao
) : ProfileRepository {
    override suspend fun getUserById(userId: Long, currentUserId: Long): Response<ProfileResponse> {
        val userRow = userDao.findById(userId = userId)
        return if  (userRow == null){
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = ProfileResponse(
                    success = false,
                    message = "Could not fine user with id: $userId"
                )
            )
        } else{
            val isFollowing = followsDao.isAlreadyFollowing(follower = currentUserId, following = userId)
            val isOwnProfile = userId == currentUserId
            Response.Success(
                data = ProfileResponse(
                    success = true,
                    profile = toProfile(userRow = userRow, isFollowing = isFollowing, isOwnProfile = isOwnProfile)
                )
            )
        }
    }

    override suspend fun updateUser(updateUserParams: UpdateUserParams): Response<ProfileResponse> {
        val userExists = userDao.findById(userId = updateUserParams.userId) != null

        if (userExists){
            val userUpdated = userDao.updateUser(
                userId = updateUserParams.userId,
                name = updateUserParams.name,
                bio = updateUserParams.bio,
                imageUrl = updateUserParams.imageUrl
            )

            return if(userUpdated){
                Response.Success(
                    data = ProfileResponse(success = true)
                )
            } else{
                Response.Error(
                    code = HttpStatusCode.Conflict,
                    data = ProfileResponse(
                        success = false,
                        message = "Could not update user: ${updateUserParams.userId}"
                    )
                )
            }
        }else{
            return Response.Error(
                code = HttpStatusCode.NotFound,
                data = ProfileResponse(
                    success = false,
                    message = "Could not find update: ${updateUserParams.userId}"
                )
            )
        }
    }

    private fun toProfile(userRow: UserRow, isFollowing: Boolean, isOwnProfile: Boolean): Profile{
        return Profile(
            id = userRow.id,
            name = userRow.name,
            bio = userRow.bio,
            imageUrl= userRow.imageUrl,
            followersCount = userRow.followersCount,
            followingCount = userRow.followingCount,
            isFollowing = isFollowing,
            isOwnProfile = isOwnProfile
        )
    }
}