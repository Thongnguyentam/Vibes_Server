package com.gawkgawk.repository.post

import com.gawkgawk.dao.follows.FollowsDao
import com.gawkgawk.dao.post.PostDao
import com.gawkgawk.dao.post.PostRow
import com.gawkgawk.dao.post_likes.PostLikesDao
import com.gawkgawk.dao.user.UserDao
import com.gawkgawk.model.Post
import com.gawkgawk.model.PostResponse
import com.gawkgawk.model.PostTextParams
import com.gawkgawk.model.PostsResponse
import com.gawkgawk.util.Response
import io.ktor.http.*

class PostRepositoryImpl(
    private val postDao: PostDao,
    private val followsDao: FollowsDao,
    private val postLikesDao: PostLikesDao
) : PostRepository {
    override suspend fun createPost(imageUrl: String, postTextParams: PostTextParams): Response<PostResponse> {
        val postIsCreated = postDao.createPost(
            caption = postTextParams.caption,
            imageUrl = imageUrl,
            userId = postTextParams.userId
        )

        return if (postIsCreated){
            Response.Success(
                data = PostResponse(
                    success = true
                )
            )
        } else {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = PostResponse(
                    success = false,
                    message = "Post could not be inserted in the db"
                )
            )
        }
    }
    private fun toPost(postRow: PostRow, isPostLiked:Boolean, isOwnPost: Boolean): Post {
        return Post(
            postId = postRow.postId,
            caption = postRow.caption,
            imageUrl = postRow.imageUrl,
            createdAt = postRow.createdAt,
            likesCount = postRow.likesCount,
            commentsCount = postRow.commentsCount,
            userId=  postRow.userId,
            userName = postRow.userName,
            userImageUrl =  postRow.userImageUrl,
            isLiked = isPostLiked,
            isOwnPost = isOwnPost
        )
    }
    override suspend fun getFeedPosts(userId: Long, pageNumber: Int, pageSize: Int): Response<PostsResponse> {
        val followingUsers = followsDao.getAllFollowing(userId = userId).toMutableList()
        followingUsers.add(userId)

        val postsRow = postDao.getFeedsPost(
            userId = userId,
            follows = followingUsers,
            pageNumber = pageNumber,
            pageSize = pageSize
        )

        val posts = postsRow.map {
            toPost(
                postRow = it,
                isPostLiked = postLikesDao.isPostLikedByUser(postId = it.postId, userId = userId),
                isOwnPost = it.userId == userId)
        }
        return Response.Success(
            data = PostsResponse(
                success = true,
                posts = posts
            )
        )
    }

    override suspend fun getPostsByUser(
        postsOwnerId: Long,
        currentUserId: Long,
        pageNumber: Int,
        pageSize: Int
    ): Response<PostsResponse> {
        val postsRows = postDao.getPostByUser(
            userId = postsOwnerId,
            pageNumber = pageNumber,
            pageSize = pageSize)

        val posts = postsRows.map {
            toPost(
                postRow = it,
                isPostLiked = postLikesDao.isPostLikedByUser(postId = it.postId, userId = currentUserId),
                isOwnPost = it.userId == currentUserId
            )
        }
        return Response.Success(
            data = PostsResponse(
                success = true,
                posts = posts
            )
        )
    }

    override suspend fun getPost(postId: Long, currentUserId: Long): Response<PostResponse> {
        val post = postDao.getPost(postId = postId)

        return if (post == null){
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = PostResponse(
                    success = false,
                    message = "Could not retrieve post from the database"
                )
            )
        } else{
            val isPostLiked = postLikesDao.isPostLikedByUser(postId, currentUserId)
            val isOwnPost = post.userId == currentUserId
            Response.Success(
                data = PostResponse(
                    success = true,
                    toPost(post, isPostLiked = isPostLiked, isOwnPost = isOwnPost)
                )
            )
        }
    }

    override suspend fun deletePost(postId: Long): Response<PostResponse> {
        val postIsDeleted = postDao.deletePost(
            postId = postId
        )

        return if (postIsDeleted){
            Response.Success(
                data = PostResponse(
                    success = true
                )
            )
        } else {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = PostResponse(
                    success = false,
                    message = "Post could not be deleted in the db"
                )
            )
        }
    }
}