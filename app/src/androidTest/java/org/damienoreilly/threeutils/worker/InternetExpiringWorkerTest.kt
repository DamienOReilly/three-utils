package org.damienoreilly.threeutils.worker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import androidx.work.testing.TestListenableWorkerBuilder
import com.google.common.truth.Truth
import kotlinx.coroutines.runBlocking
import org.damienoreilly.threeutils.repository.My3Repository
import org.damienoreilly.threeutils.repository.PreferenceStorage
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock

@RunWith(AndroidJUnit4::class)
class InternetExpiringWorkerTest: KoinTest {

    private lateinit var context: Context

    private val my3Repository: My3Repository by inject()

    private val preferenceStorage: PreferenceStorage by inject()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        declareMock<My3Repository>()
    }

    @Test
    fun test() {
        val worker = TestListenableWorkerBuilder<InternetExpiringWorker>(context).build()
        runBlocking {
            val result = worker.doWork()
            Truth.assertThat(result).isEqualTo(ListenableWorker.Result.success())
        }

        val workInfo = WorkManager.getInstance(context)
                .getWorkInfosForUniqueWork(InternetExpiringWorker.MY3_INTERNET_EXPIRING_WORKER).get()
    }
}