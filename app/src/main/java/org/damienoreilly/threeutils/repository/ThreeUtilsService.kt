package org.damienoreilly.threeutils.repository

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Deferred
import org.damienoreilly.threeutils.model.ApiError
import org.damienoreilly.threeutils.model.UnknownError

interface ThreeUtilsService {

    val errorModels: Sequence<JsonAdapter<out ApiError>>

    val moshi: Moshi
        get() = Moshi.Builder()
                .build()

    suspend fun <T : Any> call(call: suspend () -> Deferred<retrofit2.Response<T>>): Response<T> {
        val response = call.invoke().await()
        return if (response.isSuccessful) {
            Response.Success(response.body()!!)
        } else {
            val apiResponse: ApiError?
            apiResponse = response.errorBody()?.let { body ->
                val bodyStr = body.string()
                errorModels.map { parse(bodyStr, it) }
                        .firstOrNull { it != null }
            }
            Response.Error(apiResponse
                    ?: UnknownError("Unknown error occurred."))
        }
    }

    private fun <T> parse(body: String, adapter: JsonAdapter<T>): T? {
        return try {
            adapter.fromJson(body)
        } catch (j: JsonDataException) {
            null
        }
    }

    sealed class Response<out T : Any> {
        data class Success<out T : Any>(val data: T) : Response<T>()
        data class Error(val error: ApiError) : Response<Nothing>()
    }

}