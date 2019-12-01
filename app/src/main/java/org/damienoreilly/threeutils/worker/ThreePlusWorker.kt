package org.damienoreilly.threeutils.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.damienoreilly.threeutils.model.EnterCompetition
import org.damienoreilly.threeutils.repository.PreferenceStorage
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
        val preferenceStorage: PreferenceStorage by inject()

        if (preferenceStorage.threePlusUserName != null && preferenceStorage.threePlusPassword != null) {
            when (val login = threePlusRepository.login(preferenceStorage.threePlusUserName!!, preferenceStorage.threePlusPassword!!)) {
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
        } else {
            disableThreePlusWorker()
        }
        Result.success()
    }

    private fun disableThreePlusWorker() =
            WorkManager.getInstance(applicationContext).cancelUniqueWork(THREE_PLUS_WORKER)

    private fun logError(comps: Error) {
        Log.d("3Plus", comps.error.toString())
    }

    companion object {
        const val THREE_PLUS_WORKER = "three_plus_worker"
    }

}