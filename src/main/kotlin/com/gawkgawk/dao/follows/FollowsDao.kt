package com.gawkgawk.dao.follows

interface FollowsDao {
    suspend fun followUser(follower: Long, following: Long): Boolean

    suspend fun unfollowUser(follower: Long, following: Long): Boolean

    /**
     * Return a list of id
     * when this method returns the list of id, we are going to fetch the user's information
     */
    suspend fun getFollowers(userId: Long, pageNumber: Int, pageSize: Int): List<Long>

    suspend fun getFollowing(userId: Long, pageNumber: Int, pageSize: Int): List<Long>

    // return a list of user that userId is following
    suspend fun getAllFollowing(userId: Long): List<Long>

    suspend fun isAlreadyFollowing(follower: Long, following: Long): Boolean
}