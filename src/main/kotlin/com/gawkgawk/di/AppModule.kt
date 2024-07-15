package com.gawkgawk.di

import com.gawkgawk.dao.follows.FollowsDao
import com.gawkgawk.dao.follows.FollowsDaoImpl
import com.gawkgawk.dao.post.PostDao
import com.gawkgawk.dao.post.PostDaoImpl
import com.gawkgawk.dao.post_comments.PostCommentsDao
import com.gawkgawk.dao.post_comments.PostCommentsDaoImpl
import com.gawkgawk.dao.post_likes.PostLikesDao
import com.gawkgawk.dao.post_likes.PostLikesDaoImpl
import com.gawkgawk.dao.user.UserDao
import com.gawkgawk.dao.user.UserDaoImpl
import com.gawkgawk.model.FollowsParams
import com.gawkgawk.model.Post
import com.gawkgawk.repository.auth.AuthRepository
import com.gawkgawk.repository.auth.AuthRepositoryImpl
import com.gawkgawk.repository.follows.FollowsRepository
import com.gawkgawk.repository.follows.FollowsRepositoryImpl
import com.gawkgawk.repository.post.PostRepository
import com.gawkgawk.repository.post.PostRepositoryImpl
import com.gawkgawk.repository.post_comments.PostCommentsRepository
import com.gawkgawk.repository.post_comments.PostCommentsRepositoryImpl
import com.gawkgawk.repository.post_likes.PostLikesRepository
import com.gawkgawk.repository.post_likes.PostLikesRepositoryImpl
import com.gawkgawk.repository.profile.ProfileRepository
import com.gawkgawk.repository.profile.ProfileRepositoryImpl
import org.koin.dsl.module

/**
 *  this module configures the dependency injection setup for user-related data access
 *  and repository classes, making it easier to manage dependencies and promote modularity
 *  and testability in the application.
 */
val appModule = module{
    // Defines a Koin module, which is a container for defining dependencies.
    // Defines a singleton instance of the specified type T.
    // Koin will create and manage a single instance of this type throughout the application.


    // It creates an instance of AuthRepositoryImpl and passes a dependency to its constructor
    // using get(), which retrieves the required dependency from Koin’s context.
    single<AuthRepository> {AuthRepositoryImpl(get())}
    single<UserDao> {UserDaoImpl()}
    single<FollowsDao> { FollowsDaoImpl() }
    single<FollowsRepository> { FollowsRepositoryImpl(get(), get()) } // requires instances of UserDao, followsDao
    single<PostRepository> {PostRepositoryImpl(get(), get(), get())}
    single<PostDao> {PostDaoImpl()}
    single<PostLikesDao> {PostLikesDaoImpl()}
    single<PostRepository> {PostRepositoryImpl(get(), get(), get())}
    single<ProfileRepository>{ProfileRepositoryImpl(get(), get())}
    single<PostCommentsDao> {PostCommentsDaoImpl()}
    single<PostCommentsRepository> { PostCommentsRepositoryImpl(get(), get()) }
    single<PostLikesRepository> { PostLikesRepositoryImpl(get(), get()) }
}