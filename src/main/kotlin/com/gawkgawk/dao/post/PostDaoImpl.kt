package com.gawkgawk.dao.post

import com.gawkgawk.dao.DatabaseFactory.dbQuery
import com.gawkgawk.dao.user.UserTable
import com.gawkgawk.util.IdGenerator
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus

class PostDaoImpl : PostDao {
    override suspend fun createPost(caption: String, imageUrl: String, userId: Long): Boolean {
        return dbQuery{
            val insertStatement = PostTable.insert {
                it[PostTable.postId] = IdGenerator.generateId()
                it[PostTable.caption] = caption
                it[PostTable.imageUrl] = imageUrl
                it[PostTable.userId] = userId
                it[PostTable.likesCount] = 0
                it[PostTable.commentsCount] = 0
            }
            insertStatement.resultedValues?.singleOrNull() != null
        }
    }

    override suspend fun getFeedsPost(
        userId: Long,
        follows: List<Long>,
        pageNumber: Int,
        pageSize: Int
    ): List<PostRow> {
        return dbQuery{
            if (follows.size > 1) { //if users has followed someone
                getPosts(users = follows, pageSize = pageSize, pageNumber = pageNumber)
            } else{
                PostTable.join(
                    otherTable = UserTable,
                    onColumn = PostTable.userId,
                    otherColumn = UserTable.id,
                    joinType = JoinType.INNER
                )
                    .selectAll() // because they didn't follow anyone
                    .orderBy(column = PostTable.likesCount, order = SortOrder.DESC) // show popular post
                    .limit(n = pageSize, offset = ((pageNumber-1)*pageSize).toLong())
                    .map { toPostRow(it) }
            }
        }
    }

    override suspend fun getPostByUser(userId: Long, pageNumber: Int, pageSize: Int): List<PostRow> {
        return dbQuery { getPosts(users = listOf(userId), pageSize = pageSize, pageNumber = pageNumber) }

    }

    private fun getPosts(users: List<Long>, pageSize: Int, pageNumber: Int): List<PostRow>{
        return PostTable.join(
            onColumn = PostTable.userId,
            otherTable = UserTable,
            otherColumn = UserTable.id,
            joinType = JoinType.INNER
        )
            .select(where = PostTable.userId inList users)
            .orderBy(column = PostTable.createdAt, order = SortOrder.DESC)
            .limit(n = pageSize, offset = ((pageNumber-1)*pageSize).toLong())
            .map { toPostRow(it) }
    }

    override suspend fun getPost(postId: Long): PostRow? {
        return dbQuery {
            PostTable.join(
                onColumn = PostTable.userId,
                otherTable = UserTable,
                otherColumn = UserTable.id,
                joinType = JoinType.INNER
            )
                .select{PostTable.postId eq postId}
                .singleOrNull()
                ?.let{toPostRow(it)}
        }
    }

    override suspend fun deletePost (postId: Long): Boolean {
        return dbQuery {
            PostTable.deleteWhere{ PostTable.postId eq postId } > 0
        }
    }

    override suspend fun updateLikesCount(postId: Long, decrement: Boolean): Boolean {
        return dbQuery {
            val value = if (decrement) - 1 else 1
            PostTable.update(where = {PostTable.postId eq postId}) {
                it.update(column = likesCount, value = likesCount.plus(value))
            } > 0
        }
    }

    override suspend fun updateCommentsCount(postId: Long, decrement: Boolean): Boolean {
        return dbQuery {
            val value = if (decrement) -1 else 1
            PostTable.update (where = {PostTable.postId eq postId}){
                it.update(column = commentsCount, value = commentsCount.plus(value))
            }
        } > 0
    }

    private fun toPostRow(row: ResultRow): PostRow{
        return PostRow(
            postId = row[PostTable.postId],
            caption =  row[PostTable.caption],
            imageUrl = row[PostTable.imageUrl],
            createdAt = row[PostTable.createdAt].toString(),
            likesCount = row[PostTable.likesCount],
            commentsCount = row[PostTable.commentsCount],
            userId = row[PostTable.userId],
            userName = row[UserTable.name],
            userImageUrl = row[UserTable.imageUrl]
        )
    }


}