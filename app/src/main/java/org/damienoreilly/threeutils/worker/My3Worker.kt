package org.damienoreilly.threeutils.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import io.karn.notify.Notify
import kotlinx.coroutines.runBlocking
import org.damienoreilly.threeutils.R
import org.damienoreilly.threeutils.model.My3Error
import org.damienoreilly.threeutils.model.UsageDetails
import org.damienoreilly.threeutils.repository.My3Repository
import org.damienoreilly.threeutils.repository.PreferenceStorage
import org.damienoreilly.threeutils.repository.ThreeUtilsService.Response.Error
import org.damienoreilly.threeutils.repository.ThreeUtilsService.Response.Success
import org.damienoreilly.threeutils.util.Utils.getDelay
import org.damienoreilly.threeutils.util.Utils.parseDate
import org.damienoreilly.threeutils.worker.InternetExpiringWorker.Companion.MY3_INTERNET_EXPIRING_WORKER
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
                    is Error -> {
                        when (login.error) {
                            is My3Error -> {
                                if (login.error.toString().startsWith("Incorrect Username or Password.")) {
                                    // Disable periodically refreshing to prevent account from been locked out.
                                    Notify.with(applicationContext)
                                            .content {
                                                title = applicationContext.getString(R.string.my3_incorrect_credentials_short)
                                                text = applicationContext.getString(R.string.my3_incorrect_credentials)
                                            }
                                            .show()
                                    disableMy3UsageRefreshWorker()
                                } else logError(login)
                            }
                            else -> logError(login)
                        }
                    }
                }
            }
        } else {
            disableMy3UsageRefreshWorker()
        }
        return Result.success()
    }

    private fun disableMy3UsageRefreshWorker() =
            WorkManager.getInstance(applicationContext).cancelUniqueWork(MY3_USAGE_REFRESH_WORKER)

    private fun setUpNotificationIfNeeded(usageDetails: UsageDetails) {
        usageDetails.text
                ?.filter { it.name in internets }
                ?.mapNotNull { parseDate(it.expiryText) }
                ?.maxWith(ZonedDateTime.timeLineOrder())
                ?.let { setupInternetExpiringWorker(it) }
    }

    private fun setupInternetExpiringWorker(expireDate: ZonedDateTime) {
        // 🤢
        val delay = getDelay(expireDate, ZonedDateTime.now(), Duration.ofHours(4)) //FIXME: make configurable
                .toMillis()

        Log.w("My3", "Delay set to $delay")

        val work = OneTimeWorkRequestBuilder<InternetExpiringWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

        WorkManager.getInstance(applicationContext)
                .enqueueUniqueWork(MY3_INTERNET_EXPIRING_WORKER,
                        ExistingWorkPolicy.KEEP, work)
    }

    private fun logError(login: Error) {
        Log.d("My3", login.error.toString())
    }

    companion object {
        val internets = setOf("Unlimited Data in Republic of Ireland")// TODO: consider "Internet in Republic of Ireland & EU" ?

        const val MY3_USAGE_REFRESH_WORKER = "my3_usage_refresh_worker"
    }

}
