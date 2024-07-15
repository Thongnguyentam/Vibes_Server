package com.gawkgawk.model

import kotlinx.serialization.Serializable

@Serializable
data class FollowAndUnfollowResponse(
    val success: Boolean,
    val message: String? = null
)

@Serializable
data class FollowsParams(
    val follower: Long,
    val following: Long
)

@Serializable
data class FollowsUserData(
    val id: Long,
    val name: String,
    val bio: String,
    val imageUrl: String? = null,
    val isFollowing: Boolean
)

@Serializable
data class GetFollowsResponse(
    val success: Boolean,
    val follows: List<FollowsUserData> = listOf(),
    val message: String?= null
)