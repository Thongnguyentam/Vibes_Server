package com.gawkgawk.repository.follows

import com.gawkgawk.dao.follows.FollowsDao
import com.gawkgawk.dao.user.UserDao
import com.gawkgawk.dao.user.UserRow
import com.gawkgawk.model.FollowAndUnfollowResponse
import com.gawkgawk.model.FollowsUserData
import com.gawkgawk.model.GetFollowsResponse
import com.gawkgawk.util.Constants
import com.gawkgawk.util.Response
import io.ktor.http.*

class FollowsRepositoryImpl(
    private val userDao: UserDao,
    private val followsDao: FollowsDao
) : FollowsRepository {
    override suspend fun followUser(follower: Long, following: Long): Response<FollowAndUnfollowResponse> {
        return if(followsDao.isAlreadyFollowing(follower, following)){
            Response.Error(
                code = HttpStatusCode.Forbidden,
                data = FollowAndUnfollowResponse(
                    success = false,
                    message = "You are already following this user"
                )
            )
        } else{
            val success = followsDao.followUser(follower, following)

            if (success){
                userDao.updateFollowsCount(follower, following, isFollowing = true)
                Response.Success(
                    data = FollowAndUnfollowResponse(success = true)
                )
            } else{
                Response.Error(
                    code = HttpStatusCode.InternalServerError,
                    data = FollowAndUnfollowResponse(
                        success = false,
                        message = "Oops, something went wrong on our side, please try again"
                    )
                )
            }
        }
    }

    override suspend fun unfollowUser(follower: Long, following: Long): Response<FollowAndUnfollowResponse> {
        val success = followsDao.unfollowUser(follower, following)

        return if (success){
            userDao.updateFollowsCount(follower, following, isFollowing = false)
            Response.Success(
                data = FollowAndUnfollowResponse(
                    success = true
                )
            )
        } else{
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = FollowAndUnfollowResponse(
                    success = false,
                    message = "Oops, something went wrong on our side, please try again"
                )
            )
        }
    }

    override suspend fun getFollowers(userId: Long, pageNumber: Int, pageSize: Int): Response<GetFollowsResponse> {
        val followerIDs = followsDao.getFollowers(userId = userId, pageNumber = pageNumber, pageSize = pageSize)
        val followingRows = userDao.getUsers(ids = followerIDs)
        val followers =  followingRows.map { followerRow ->
            val isFollowing = followsDao.isAlreadyFollowing(follower = followerRow.id, following = userId)
            toFollowUserData(userRow = followerRow, isFollowing = isFollowing)
        }
        return Response.Success(
            data = GetFollowsResponse(success = true, follows = followers)
        )
    }

    override suspend fun getFollowing(userId: Long, pageNumber: Int, pageSize: Int): Response<GetFollowsResponse> {
        val followingIds = followsDao.getFollowing(userId, pageNumber = pageNumber, pageSize = pageSize)
        val followingRows = userDao.getUsers(ids = followingIds)
        val followings = followingRows.map{ followingRow ->
            toFollowUserData(userRow = followingRow, isFollowing = true)
        }
        return Response.Success(
            data = GetFollowsResponse(success = true, follows = followings)
        )
    }

    override suspend fun getFollowingSuggestions(userId: Long): Response<GetFollowsResponse> {
        // get all users that userId is following
        val hasFollowing = followsDao.getFollowing(userId = userId, pageNumber = 0, pageSize = 1).isNotEmpty()
        return if (hasFollowing){
            Response.Error(
                code = HttpStatusCode.Forbidden,
                data = GetFollowsResponse(
                    success = false,
                    message = "User has following"
                )
            )
        }
        // if userId hasn't followed anyone
        else{
            val suggestedFollowingRows = userDao.getPopularUser(limit = Constants.SUGGESTED_FOLLOWING_LIMIT)
            // Returns a list containing all elements not matching the given predicate.
            val suggestedFollowing = suggestedFollowingRows.filterNot {
                it.id == userId
            }.map {
                toFollowUserData(userRow = it, isFollowing = false)
            }
            return Response.Success(
                data = GetFollowsResponse(success = true, follows = suggestedFollowing)
            )
        }
    }

    private fun toFollowUserData(userRow: UserRow, isFollowing: Boolean): FollowsUserData{
        return FollowsUserData(
            id = userRow.id,
            name = userRow.name,
            bio = userRow.bio,
            imageUrl = userRow.imageUrl,
            isFollowing = isFollowing
        )
    }
}