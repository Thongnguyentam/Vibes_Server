package com.gawkgawk.dao.user

import com.gawkgawk.dao.DatabaseFactory.dbQuery
import com.gawkgawk.model.SignUpParams
import com.gawkgawk.security.hashPassword
import com.gawkgawk.util.IdGenerator
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus

/**
 * Implementation of the UserDao interface for handling user data operations.
 * Data Access Object is used to handle user data operations
 */
class UserDaoImpl : UserDao {
    // A suspend function in Kotlin is a function that can be paused and resumed later.
    // It's used in coroutines to perform long-running tasks without blocking the main thread.
    /**
     * Inserts a new user into the database.
     * @param params The parameters required for user sign-up.
     * @return The inserted User object if successful, otherwise null.
     */
    override suspend fun insert(params: SignUpParams): UserRow? {
        return dbQuery {
            val insertStatement = UserTable.insert {
                it[id] = IdGenerator.generateId()
                it[name] = params.name
                it[email] = params.email
                it[password] = hashPassword( params.password)
            }

            // resultedValues contains the rows affected by the insert operation,
            // which in this case is the row that was inserted.
            // If it is not null, singleOrNull() is called
            // singleOrNull() returns the single element if there is exactly one element in the collection,
            // or null if the collection is empty or has more than one element.
            insertStatement.resultedValues?.singleOrNull()?.let{
                // If singleOrNull() returns a non-null value, let is called with that value (it).
                // The let function executes rowToUser(it)
                // rowToUser(it) converts the ResultRow to a User object.
                rowToUser(it)
            }
        }
    }
    /**
     * Converts a ResultRow to a User object.
     * @param it The ResultRow to convert.
     * @return The converted User object.
     */
    private fun rowToUser(it: ResultRow): UserRow {
        return UserRow(
            id = it[UserTable.id],
            name = it[UserTable.name],
            bio = it[UserTable.bio],
            password = it[UserTable.password],
            imageUrl = it[UserTable.imageUrl],
            followersCount = it[UserTable.followersCount],
            followingCount = it[UserTable.followingCount]
        )
    }

    /**
     * Finds a user by their email.
     * @param email The email of the user to find.
     * @return The User object if found, otherwise null.
     */
    override suspend fun findByEmail(email: String): UserRow? {
        return dbQuery {
            UserTable.select(UserTable.email eq email)
                .map{rowToUser(it)}
                .singleOrNull()
        }
    }

    override suspend fun updateUser(userId: Long, name: String, bio: String, imageUrl: String?): Boolean {
        return dbQuery {
            UserTable.update (where = {UserTable .id eq userId}){
                it[UserTable.name] = name
                it[UserTable.imageUrl] = imageUrl
                it[UserTable.bio] = bio
            } > 0
        }
    }

    override suspend fun findById(userId: Long): UserRow? {
        return dbQuery {
            UserTable.select(UserTable.id eq userId)
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    /**
     * One following the others -> following + 1
     * One being followed -> followed + 1
     * follower: Long: The ID of the user who is following or unfollowing.
     * following: Long: The ID of the user who is being followed or unfollowed.
     * isFollowing: Boolean: Indicates whether the action is following (true) or unfollowing (false).
     */
    override suspend fun updateFollowsCount(follower: Long, following: Long, isFollowing: Boolean): Boolean {
        return dbQuery {
            val count = if (isFollowing) +1 else -1

            // those follow the other
            val success1 = UserTable.update({UserTable.id eq follower}){
                // Updates the followersCount column for the user with ID following.
                it.update(column = followingCount, value = followingCount.plus(count))
            } > 0 // checks if the update affected at least one row, indicating success.

            // those being followed
            val success2 = UserTable.update({UserTable.id eq following}) {
                it.update(column = followersCount, value = followersCount.plus(count))
            } > 0 // checks if the update affected at least one row, indicating success.
            success1 && success2
        }
    }

    override suspend fun getUsers(ids: List<Long>): List<UserRow> {
        return dbQuery {
            UserTable.select { (UserTable.id inList ids) }
                .map { rowToUser(it) }
        }

    }

    override suspend fun getPopularUser(limit: Int): List<UserRow> {
        return dbQuery {
            UserTable.selectAll()
                .orderBy(column = UserTable.followersCount, order = SortOrder.DESC)
                .limit(n = limit)
                .map { rowToUser(it) }
        }
    }
}