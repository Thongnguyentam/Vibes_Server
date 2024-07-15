package com.gawkgawk.repository.post

import com.gawkgawk.model.Post
import com.gawkgawk.model.PostResponse
import com.gawkgawk.model.PostTextParams
import com.gawkgawk.model.PostsResponse
import com.gawkgawk.util.Response

interface PostRepository {
    suspend fun createPost(imageUrl: String, postTextParams: PostTextParams): Response<PostResponse>

    suspend fun getFeedPosts(userId: Long, pageNumber: Int, pageSize: Int): Response<PostsResponse>

    suspend fun getPostsByUser(
        postsOwnerId: Long,
        currentUserId: Long,
        pageNumber: Int,
        pageSize: Int
    ): Response<PostsResponse>

    suspend fun getPost(postId: Long, currentUserId: Long): Response<PostResponse>

    suspend fun deletePost(postId: Long): Response<PostResponse>

}