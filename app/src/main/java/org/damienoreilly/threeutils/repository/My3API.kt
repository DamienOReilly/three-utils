package org.damienoreilly.threeutils.repository

import kotlinx.coroutines.Deferred
import org.damienoreilly.threeutils.model.Balance
import org.damienoreilly.threeutils.model.Login
import org.damienoreilly.threeutils.model.My3Token
import org.damienoreilly.threeutils.model.UsageDetails
import retrofit2.Response
import retrofit2.http.*

interface My3API {

    @POST("auth/login/v1.0")
    fun loginAsync(@Body login: Login): Deferred<Response<My3Token>>

    @GET("usage/balance/v1.0")
    fun getBalanceAsync(@Header("Authorization") token: String, @Query("ctn") ctn: String): Deferred<Response<Balance>>

    @GET("usage/quick/v1.0")
    fun getUsageDetailsAsync(@Header("Authorization") token: String, @Query("ctn") ctn: String): Deferred<Response<UsageDetails>>

}
