package org.damienoreilly.threeutils.repository

import com.squareup.moshi.JsonAdapter
import org.damienoreilly.threeutils.model.*
import org.damienoreilly.threeutils.repository.ThreeUtilsService.*


interface ThreePlusRepository: ThreeUtilsService {
    suspend fun login(username: String, password: String): Response<ThreePlusToken>
    suspend fun getCompetitions(token: String): Response<List<Competitions>>
    suspend fun enterCompetition(token: String, offer: Number, comp: EnterCompetition): Response<CompetitionEntered>
}


class ThreePlusRepositoryImpl(private val threePlusAPI: ThreePlusAPI) : ThreePlusRepository {

    override val errorModels: Sequence<JsonAdapter<out ApiError>>
        get() = sequenceOf(
                moshi.adapter(ThreePlusFatalError::class.java).failOnUnknown(),
                moshi.adapter(ThreePlusCompetitionEnteringError::class.java).failOnUnknown(),
                moshi.adapter(ThreePlusRequestError::class.java).failOnUnknown()
        )

    override suspend fun login(username: String, password: String) = call {
        threePlusAPI.loginAsync(username = username, password = password)
    }

    override suspend fun getCompetitions(token: String) = call {
        threePlusAPI.getCompetitionsAsync("Bearer $token")
    }

    override suspend fun enterCompetition(token: String, offer: Number, comp: EnterCompetition) = call {
        threePlusAPI.enterCompetitionAsync("Bearer $token", offer, comp)
    }

}

