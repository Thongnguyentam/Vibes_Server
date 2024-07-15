package com.gawkgawk.route

import com.gawkgawk.model.PostResponse
import com.gawkgawk.model.PostTextParams
import com.gawkgawk.model.PostsResponse
import com.gawkgawk.repository.post.PostRepository
import com.gawkgawk.util.Constants
import com.gawkgawk.util.getLongParameter
import com.gawkgawk.util.saveFile
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import java.io.File

fun Routing.postRouting() {
    val postRepository by inject<PostRepository>()
    authenticate{
        route(path = "/post") {
            post(path = "/create") {
                var fileName = ""
                var postTextParams: PostTextParams? = null
                val multiPartData = call.receiveMultipart()
                // loop iterates through each part of the multipart data.
                multiPartData.forEachPart { partData ->
                    when(partData){
                        // If the part is a file, it saves the file to a specified
                        // directory (Constants.POST_IMAGES_FOLDER_PATH) and updates
                        // the fileName variable with the path where the file was saved.
                        is PartData.FileItem -> {
                            fileName = partData.saveFile(folderPath = Constants.POST_IMAGES_FOLDER_PATH)
                        }

                        is PartData.FormItem -> {
                            if (partData.name == "post_data"){
                                postTextParams = Json.decodeFromString(partData.value)
                            }
                        }
                        else -> {}
                    }
                    // After processing each part, this ensures that any resources tied
                    // to that part are freed, which is important for handling file uploads
                    // to prevent memory leaks.
                    partData.dispose()
                }

                val imageUrl = "${Constants.BASE_URL}${Constants.POST_IMAGES_FOLDER}$fileName"

                if (postTextParams == null){ // if we cannot decode
                    // delete the file we saved
                    File("${Constants.POST_IMAGES_FOLDER_PATH}/${fileName}").delete()
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = PostResponse(
                            success = false,
                            message = "Could not parse post data"
                        )
                    )
                } else{
                    val result = postRepository.createPost(imageUrl, postTextParams!!)
                    call.respond(result.code, message = result.data)
                }
            }

            get(path = "/{postId}"){
                try{
                    val postId = call.getLongParameter(name = "postId")
                    val currentUserId = call.getLongParameter(name = "currentUserId", isQueryParameter = true)

                    val result = postRepository.getPost(postId = postId, currentUserId = currentUserId)
                    call.respond(status = result.code, message = result.data)
                } catch (badRequestError: BadRequestException){
                    return@get // The return@get statement stops the further execution of the current get block.
                    // It effectively exits the get block without executing any further code within it.
                } catch (anyError: Throwable){
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = PostResponse(
                            success = false,
                            message = "An unexpected has occurred, try again!"
                        )
                    )
                }
            }

            delete(path = "/{postId}"){
                try{
                    val postId = call.getLongParameter(name = "postId")
                    val result = postRepository.deletePost(postId = postId)

                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                } catch (badRequestError: BadRequestException){
                    return@delete
                } catch (anyError: Throwable){
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = PostResponse(
                            success = false,
                            message = "An unexpected error has occurred, try again!"
                        )
                    )
                }
            }
        }

        route(path = "posts"){
            get(path = "feed"){
                try{
                    val currentUserId = call.getLongParameter(name = "currentUserId", isQueryParameter = true)
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                    val result = postRepository.getFeedPosts(
                        userId = currentUserId,
                        pageNumber = page,
                        pageSize = limit
                    )
                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                } catch (badRequestError : BadRequestException){
                    return@get
                } catch (anyError: Throwable){
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = PostsResponse(
                            success = false,
                            message = "An unexpected error has occurred, try again!"
                        )
                    )
                }
            }

            get(path = "/{userId}"){
                try{
                    val postsOwnerId = call.getLongParameter(name = "userId")
                    val currentUserId = call.getLongParameter(name = "currentUserId", isQueryParameter = true)
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                    val result = postRepository.getPostsByUser(
                        postsOwnerId = postsOwnerId,
                        currentUserId= currentUserId,
                        pageNumber= page,
                        pageSize= limit
                    )

                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                } catch(badRequestError: BadRequestException){
                    return@get
                } catch(anyError: Throwable){
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = PostsResponse(
                            success = false,
                            message = "An unexpected error has occurred, try again!"
                        )
                    )
                }
            }
        }
    }
}