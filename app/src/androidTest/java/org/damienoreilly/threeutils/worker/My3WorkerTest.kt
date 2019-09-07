package org.damienoreilly.threeutils.worker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker.Result
import androidx.work.WorkManager
import androidx.work.testing.TestListenableWorkerBuilder
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.damienoreilly.threeutils.model.*
import org.damienoreilly.threeutils.repository.My3Repository
import org.damienoreilly.threeutils.repository.PreferenceStorage
import org.damienoreilly.threeutils.repository.ThreeUtilsService.Response
import org.damienoreilly.threeutils.worker.My3Worker.Companion.MY3_INTERNET_EXPIRING_WORKER
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.anyString
import org.mockito.BDDMockito.given

@RunWith(AndroidJUnit4::class)
class My3WorkerTest : KoinTest {

    private lateinit var context: Context

    private val my3Repository: My3Repository by inject()

    private val preferenceStorage: PreferenceStorage by inject()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        declareMock<My3Repository>()
    }

    @Test
    fun testMy3Worker_checkInternetExpiringJobGetsCreated() {

        val token = My3Token(
                TokenInfo("", ""),
                Profile("", emptyList(), "", "")
        )

        val usage = UsageDetails("Prepay",
                emptyList(),
                emptyList(),
                listOf(Usage("Unlimited Data in Republic of Ireland",
                        "Expires 02/10/19",
                        "2,095,930")
                )
        )

        given(runBlocking {
            my3Repository.login(anyString(), anyString())
        }).willReturn(Response.Success(token))

        given(runBlocking {
            my3Repository.getUsageDetails(anyString(), anyString())
        }).willReturn(Response.Success(usage))

        preferenceStorage.my3UserName = "0870000000"
        preferenceStorage.my3Password = "testpass"

        val worker = TestListenableWorkerBuilder<My3Worker>(context).build()
        runBlocking {
            val result = worker.doWork()
            assertThat(result).isEqualTo(Result.success())
        }

        val workInfo = WorkManager.getInstance(context)
                .getWorkInfosForUniqueWork(MY3_INTERNET_EXPIRING_WORKER).get()

        assertThat(workInfo.size).isEqualTo(1)
    }

}