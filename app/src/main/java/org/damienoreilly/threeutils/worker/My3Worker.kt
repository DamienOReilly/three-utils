package org.damienoreilly.threeutils.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import kotlinx.coroutines.*
import org.damienoreilly.threeutils.model.Usage
import org.damienoreilly.threeutils.model.UsageDetails
import org.damienoreilly.threeutils.repository.My3Repository
import org.damienoreilly.threeutils.repository.PreferenceStorage
import org.damienoreilly.threeutils.repository.ThreeUtilsService.Response.*
import org.damienoreilly.threeutils.util.Utils.getDelay
import org.damienoreilly.threeutils.util.Utils.parseDate
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime
import java.util.concurrent.TimeUnit


class My3Worker(
        context: Context, workerParams: WorkerParameters
) : Worker(context, workerParams), KoinComponent {

    override fun doWork(): Result {
        val my3Repository: My3Repository by inject()
        val preferenceStorage: PreferenceStorage by inject()

        if (preferenceStorage.my3UserName != null && preferenceStorage.my3Password != null) {
            runBlocking {
                when (val login = my3Repository.login(preferenceStorage.my3UserName!!, preferenceStorage.my3Password!!)) {
                    is Success -> {
                        when (val usageDetails = my3Repository.getUsageDetails(
                                login.data.token?.accessToken!!, preferenceStorage.my3UserName!!)) {
                            is Success -> setUpNotificationIfNeeded(usageDetails.data)
                            is Error   -> logError(usageDetails)
                        }
                    }
                    is Error -> logError(login)
                }
            }
        }
        return Result.success()
    }

    private fun setUpNotificationIfNeeded(usageDetails: UsageDetails) {
        usageDetails.text
                ?.filter { it.name in internets }
                ?.mapNotNull { parseDate(it.expiryText) }
                ?.maxWith(ZonedDateTime.timeLineOrder())
                ?.let { setupInternetExpiringWorker(it) }
    }

    private fun setupInternetExpiringWorker(expireDate: ZonedDateTime) {
        // 🤢
        val delay = getDelay(expireDate, ZonedDateTime.now(), Duration.ofHours(4))
                .toMillis()

        Log.w("My3", "Delay set to $delay")

        val work = OneTimeWorkRequestBuilder<InternetExpiringWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

        WorkManager.getInstance()
                .enqueueUniqueWork("my3_internet_expiring",
                        ExistingWorkPolicy.KEEP, work)
    }

    private fun logError(login: Error) {
        Log.d("My3", login.error.toString())
    }

    companion object {
        val internets = setOf("Unlimited Data in Republic of Ireland")// TODO: consider "Internet in Republic of Ireland & EU" ?
    }

}
