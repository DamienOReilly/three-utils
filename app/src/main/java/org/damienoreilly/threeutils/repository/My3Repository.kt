package org.damienoreilly.threeutils.repository

import com.squareup.moshi.JsonAdapter
import org.damienoreilly.threeutils.model.*
import org.damienoreilly.threeutils.repository.ThreeUtilsService.Response


interface My3Repository {
    suspend fun login(username: String, password: String): Response<My3Token>
    suspend fun getBalance(token: String, username: String): Response<Balance>
    suspend fun getUsageDetails(token: String, username: String): Response<UsageDetails>
}


class My3RepositoryImpl(private val my3Api: My3API) : My3Repository, ThreeUtilsService {

    override val errorModels: Sequence<JsonAdapter<out ApiError>>
        get() = sequenceOf(moshi.adapter(My3Error::class.java).failOnUnknown())

    override suspend fun login(username: String, password: String) = call {
        my3Api.loginAsync(Login(username, password))
    }

    override suspend fun getBalance(token: String, username: String) = call {
        my3Api.getBalanceAsync("Bearer $token", username)
    }

    override suspend fun getUsageDetails(token: String, username: String) = call {
        my3Api.getUsageDetailsAsync("Bearer $token", username)
    }

}

