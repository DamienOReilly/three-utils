package org.damienoreilly.threeutils.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.damienoreilly.threeutils.model.EnterCompetition
import org.damienoreilly.threeutils.repository.ThreePlusRepository
import org.damienoreilly.threeutils.repository.ThreeUtilsService.Response.Error
import org.damienoreilly.threeutils.repository.ThreeUtilsService.Response.Success
import org.koin.core.KoinComponent
import org.koin.core.inject

class ThreePlusWorker(
        context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    override suspend fun doWork(): Result = coroutineScope {

        val threePlusRepository: ThreePlusRepository by inject()

        val prefs = applicationContext.getSharedPreferences("3plus",
                Context.MODE_PRIVATE)
        val username = prefs.getString("username", null)
        val password = prefs.getString("password", null)

        if (username != null && password != null) {
            when (val login = threePlusRepository.login(username, password)) {
                is Success -> {
                    when (val comps = threePlusRepository.getCompetitions(login.data.access_token)) {
                        is Success -> {
                            comps.data.filter { it.remaining == 1 }
                                    .map {
                                        async {
                                            threePlusRepository.enterCompetition(login.data.access_token,
                                                    it.id, EnterCompetition(offerName = it.name))
                                        }
                                    }.awaitAll()
                        }
                        is Error -> logError(comps)
                    }
                }
                is Error -> logError(login)
            }
        }
        Result.success()
    }

    private fun logError(comps: Error) {
        Log.d("3Plus", comps.error.toString())
    }

}