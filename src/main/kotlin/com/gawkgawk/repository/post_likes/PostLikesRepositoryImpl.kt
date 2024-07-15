package com.gawkgawk.repository.post_likes

import com.gawkgawk.dao.post.PostDao
import com.gawkgawk.dao.post_likes.PostLikesDao
import com.gawkgawk.model.LikeParams
import com.gawkgawk.model.LikeResponse
import com.gawkgawk.util.Response
import io.ktor.http.*

class PostLikesRepositoryImpl(
    private val likesDao: PostLikesDao,
    private val postDao: PostDao
) : PostLikesRepository {
    override suspend fun addLike(params: LikeParams): Response<LikeResponse> {
        val likeExists = likesDao.isPostLikedByUser(postId = params.postId, userId = params.userId)
        return if (likeExists){
            Response.Error(
                code = HttpStatusCode.Forbidden,
                data = LikeResponse(success = false, message = "Post already liked")
            )
        } else{
            val postLiked = likesDao.addLike(postId = params.postId, userId = params.userId)
            if (postLiked){
                postDao.updateLikesCount(postId = params.postId)
                Response.Success(
                    data = LikeResponse(success = true)
                )
            } else{
                Response.Error(
                    code = HttpStatusCode.Conflict,
                    data = LikeResponse(success = false, message = "Unexpected DB error, try again!")
                )
            }
        }
    }

    override suspend fun removeLike(params: LikeParams): Response<LikeResponse> {
        val likeExist = likesDao.isPostLikedByUser(postId = params.postId, userId = params.userId)

        return if (likeExist){
            val likeRemoved = likesDao.removeLike(postId = params.postId, userId = params.userId)
            if (likeRemoved){
                postDao.updateLikesCount(postId = params.postId, decrement = true)
                Response.Success(
                    data = LikeResponse(success = true)
                )
            } else{
                Response.Error(
                    code = HttpStatusCode.Conflict,
                    data = LikeResponse(success = false, message = "Unexpected DB error, try again!")
                )
            }
        } else{
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = LikeResponse(success = false, message = "Like not found(may be removed already)")
            )
        }
    }
}