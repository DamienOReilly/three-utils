package org.damienoreilly.threeutils.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.karn.notify.Notify
import kotlinx.coroutines.*
import org.damienoreilly.threeutils.R
import org.damienoreilly.threeutils.model.UsageDetails
import org.damienoreilly.threeutils.repository.My3Repository
import org.damienoreilly.threeutils.repository.PreferenceStorage
import org.damienoreilly.threeutils.repository.ThreeUtilsService.Response.*
import org.damienoreilly.threeutils.util.Utils.parseDate
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter


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
                            is Error -> logError(usageDetails)
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
                ?.map { parseDate(it.expiryText) }
                ?.maxWith(ZonedDateTime.timeLineOrder())
                .also {
                    Log.d("My3", it.toString())
//                    Notify.with(applicationContext)
//                            .content {
//                                title = applicationContext.getString(R.string.internet_expire_short)
//                                text = "${applicationContext.getString(R.string.internet_expire)} $it"
//                            }
//                            .show()
                }
    }

    private fun logError(login: Error) {
        Log.d("My3", login.error.toString())
    }

    companion object {
        val internets = setOf("Unlimited Data in Republic of Ireland", "Internet in Republic of Ireland & EU")
    }

}
