package com.gawkgawk.repository.post_likes

import com.gawkgawk.model.LikeParams
import com.gawkgawk.model.LikeResponse
import com.gawkgawk.util.Response

interface PostLikesRepository{
    suspend fun addLike(params: LikeParams): Response<LikeResponse>

    suspend fun removeLike(params: LikeParams): Response<LikeResponse>
}