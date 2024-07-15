package com.gawkgawk.repository.post_comments

import com.gawkgawk.model.CommentResponse
import com.gawkgawk.model.GetCommentsResponse
import com.gawkgawk.model.NewCommentParams
import com.gawkgawk.model.RemoveCommentParams
import com.gawkgawk.util.Response

interface PostCommentsRepository {
    suspend fun addComment(params: NewCommentParams): Response<CommentResponse>

    suspend fun removeComment(params: RemoveCommentParams): Response<CommentResponse>

    suspend fun getPostComments(postId: Long, pageNumber: Int, pageSize: Int): Response<GetCommentsResponse>
}