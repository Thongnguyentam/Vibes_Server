package com.gawkgawk.dao.post_likes

import com.gawkgawk.dao.DatabaseFactory.dbQuery
import com.gawkgawk.dao.post.PostTable
import com.gawkgawk.dao.user.UserTable
import com.gawkgawk.util.IdGenerator
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class PostLikesDaoImpl : PostLikesDao {
    override suspend fun addLike(postId: Long, userId: Long): Boolean {
        return dbQuery{
            val insertStatement = PostLikesTable.insert {
                it[likeId] = IdGenerator.generateId()
                it[PostLikesTable.postId] = postId
                it[PostLikesTable.userId] = userId
            }
            insertStatement.resultedValues?.isNotEmpty()?: false
        }
    }

    override suspend fun removeLike(postId: Long, userId: Long): Boolean {
        return dbQuery {
            PostLikesTable
                .deleteWhere { (PostLikesTable.postId eq postId) and (PostLikesTable.userId eq userId) } > 0
        }
    }

    override suspend fun isPostLikedByUser(postId: Long, userId: Long): Boolean {
        return dbQuery {
            PostLikesTable
                .select{(PostLikesTable.postId eq postId) and (PostLikesTable.userId eq userId)}
                .toList()
                .isNotEmpty()
        }
    }
}