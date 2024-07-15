package com.gawkgawk.dao.follows

import com.gawkgawk.dao.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class FollowsDaoImpl : FollowsDao {
    /**
     * Adds a new record to the follows table indicating that
     * the user with ID follower is now following the user
     * with ID following.
     * @return true if the insertion is successful, false otherwise.
     */
    override suspend fun followUser(follower: Long, following: Long): Boolean {
        return dbQuery{
            val insertStatement = FollowsTable.insert {
                it[followerId] = follower
                it[followingId] = following
            }
            // Returns true if the insert was successful.
            insertStatement.resultedValues?.singleOrNull() != null
        }
    }

    /**
     * Deletes a record from the follows table where
     * the user with ID follower is following the user with ID following.
     * @return Returns true if at least one record is deleted,
     * indicating the unfollow action was successful, false otherwise.
     */
    override suspend fun unfollowUser(follower: Long, following: Long): Boolean {
        return dbQuery {
            FollowsTable.deleteWhere {
                (followerId eq follower) and (followingId eq following)
            } > 0
        }
    }

    /**
     * Retrieves a paginated list of followers of the user with ID userId.
     * Results are ordered by the follow_date in descending order.
     * Returns a list of user IDs of the followers.
     *
     * pageNumber: Indicates which page of results to retrieve.
     * pageSize: Indicates how many items to include on each page.
     */
    override suspend fun getFollowers(userId: Long, pageNumber: Int, pageSize: Int): List<Long> {
        return dbQuery {
            FollowsTable.select{
                FollowsTable.followingId eq userId
            }
                .orderBy(FollowsTable.followDate, SortOrder.DESC)
                // n: specifies the limit of rows to be fetched from the database.
                // offset: starting point from where the rows (items) are fetched in the query.
                .limit(n = pageSize, offset = ((pageNumber - 1)*pageSize).toLong())
                .map { it[FollowsTable.followerId] } // Maps the results to a list of "following_id".

        }
    }

    /**
     * Retrieves a paginated list of user IDs that the user with ID userId is following (being followed).
     * Results are ordered by the follow_date in descending order.
     * Returns a list of user IDs of the users being followed.
     *
     *
     * pageNumber: Indicates which page of results to retrieve.
     * pageSize: Indicates how many items to include on each page.
     */
    override suspend fun getFollowing(userId: Long, pageNumber: Int, pageSize: Int): List<Long> {
        return dbQuery {
            FollowsTable.select{
                FollowsTable.followerId eq userId
            }
                .orderBy(FollowsTable.followDate, SortOrder.DESC)
                // n: specifies the limit of rows to be fetched from the database.
                // offset: starting point from where the rows (items) are fetched in the query.
                .limit(n = pageSize, offset = ((pageNumber - 1)*pageSize).toLong())
                .map { it[FollowsTable.followingId] }

        }
    }

    /**
     * return a list of user that userId is following
     */
    override suspend fun getAllFollowing(userId: Long): List<Long> {
        return dbQuery {
            FollowsTable
                .select{FollowsTable.followerId eq userId}
                .map { it[FollowsTable.followingId] }
        }
    }

    /**
     * Checks if a record exists in the follows table where
     * the user with ID follower is following the user with ID following.
     * Returns true if such a record exists, false otherwise.
     */
    override suspend fun isAlreadyFollowing(follower: Long, following: Long): Boolean {
        return dbQuery {
            val queryResult = FollowsTable.select{
                (FollowsTable.followerId eq follower) and (FollowsTable.followingId eq following)

            }

            queryResult.toList().isNotEmpty()
        }
    }
}