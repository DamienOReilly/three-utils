package org.damienoreilly.threeutils.repository

import kotlinx.coroutines.Deferred
import org.damienoreilly.threeutils.model.CompetitionEntered
import org.damienoreilly.threeutils.model.Competitions
import org.damienoreilly.threeutils.model.EnterCompetition
import org.damienoreilly.threeutils.model.ThreePlusToken
import retrofit2.Response
import retrofit2.http.*

interface ThreePlusAPI {

    @FormUrlEncoded
    @POST("/core/oauth/token")
    fun loginAsync(@Header("Authorization") basicAuth: String = "Basic Y2xpZW50aWQ6c2VjcmV0" /*clientid:secret*/,
                   @Field("grant_type") grantType: String = "password",
                   @Field("username") username: String,
                   @Field("password") password: String): Deferred<Response<ThreePlusToken>>

    @GET("/core/offers/competitions")
    fun getCompetitionsAsync(@Header("Authorization") token: String): Deferred<Response<List<Competitions>>>

    @PUT("/core/offers/{offer}/competitions/purchase")
    fun enterCompetitionAsync(@Header("Authorization") token: String,
                              @Path(value = "offer") offer: Number, @Body competition: EnterCompetition): Deferred<Response<CompetitionEntered>>

}
