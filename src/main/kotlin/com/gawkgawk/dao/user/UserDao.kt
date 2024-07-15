package com.gawkgawk.dao.user

import com.gawkgawk.model.SignUpParams

/**
 * Interface for user data access operations.
 */
interface UserDao {
    // return User
    /**
     * Inserts a new user into the database.
     * @param params The parameters required for user sign-up.
     * @return The inserted User object if successful, otherwise null.
     */
    suspend fun insert(params: SignUpParams): UserRow?

    /**
     * Finds a user by their email.
     * @param email The email of the user to find.
     * @return The User object if found, otherwise null.
     */
    suspend fun findByEmail(email: String): UserRow?

    suspend fun updateUser(userId: Long, name: String, bio: String, imageUrl: String?): Boolean

    suspend fun findById(userId: Long): UserRow?

    suspend fun updateFollowsCount(follower: Long, following: Long, isFollowing: Boolean): Boolean

    suspend fun getUsers(ids: List<Long>): List<UserRow>

    suspend fun getPopularUser(limit: Int): List<UserRow>
}