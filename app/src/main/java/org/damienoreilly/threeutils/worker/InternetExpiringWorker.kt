package org.damienoreilly.threeutils.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.karn.notify.Notify
import org.damienoreilly.threeutils.R

class InternetExpiringWorker(
        context: Context, workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Notify.with(applicationContext)
                .content {
                    title = applicationContext.getString(R.string.internet_expire_short)
                    text = applicationContext.getString(R.string.internet_expire)
                }
                .show()
        return Result.success()
    }

}