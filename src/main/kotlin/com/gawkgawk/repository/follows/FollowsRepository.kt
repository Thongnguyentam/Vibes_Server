package com.gawkgawk.repository.follows

import com.gawkgawk.model.FollowAndUnfollowResponse
import com.gawkgawk.model.GetFollowsResponse
import com.gawkgawk.util.Response

interface FollowsRepository {
    suspend fun followUser(follower: Long, following: Long): Response<FollowAndUnfollowResponse>

    suspend fun unfollowUser(follower: Long, following: Long): Response<FollowAndUnfollowResponse>

    suspend fun getFollowers(userId: Long, pageNumber: Int, pageSize: Int): Response<GetFollowsResponse>

    suspend fun getFollowing(userId: Long, pageNumber: Int, pageSize: Int): Response<GetFollowsResponse>

    suspend fun getFollowingSuggestions(userId: Long): Response<GetFollowsResponse>
}