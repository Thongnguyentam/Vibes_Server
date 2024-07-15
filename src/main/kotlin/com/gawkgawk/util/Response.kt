package com.gawkgawk.util

import io.ktor.http.*

/**
 * generic sealed class: Response is a sealed class, meaning it can only be subclassed within the same file.
 *  This is useful for representing a restricted class hierarchy.
 *  No other subclasses may appear outside the module and package within which the sealed class is defined.
 *
 *
 * The Response class and its subclasses (Success and Error) are used to
 * represent the outcome of an operation that can either succeed or fail.
 */
sealed class Response<T> (
    val code: HttpStatusCode = HttpStatusCode.OK,
    val data: T
){
    // The Success class is also generic and inherits from Response<T>
    // Calls the Response constructor with data and uses the default HttpStatusCode.OK for code.
    //Response.Success<T> represents a successful operation with data.

  class Success<T>(data: T): Response<T>(data = data)

    //Response.Error<T> represents a failed operation with an error code and data.
  class Error<T>(
      code:HttpStatusCode,
      data: T
  ) : Response<T>(code, data)
}