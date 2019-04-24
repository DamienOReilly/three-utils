package org.damienoreilly.threeutils.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.*
import org.damienoreilly.threeutils.model.EnterCompetition
import org.damienoreilly.threeutils.repository.ThreePlusRepository
import org.damienoreilly.threeutils.repository.ThreeUtilsService.Response.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class ThreePlusWorker(
        context: Context, workerParams: WorkerParameters
) : Worker(context, workerParams), KoinComponent {

    override fun doWork(): Result {

        val threePlusRepository: ThreePlusRepository by inject()

        val prefs = applicationContext.getSharedPreferences("3plus",
                Context.MODE_PRIVATE)
        val username = prefs.getString("username", null)
        val password = prefs.getString("password", null)

        if (username != null && password != null) {
            runBlocking {
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
        }
        return Result.success()
    }

    private fun logError(comps: Error) {
        Log.d("3Plus", comps.error.toString())
    }

}