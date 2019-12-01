package org.damienoreilly.threeutils.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import androidx.work.impl.OperationImpl
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.damienoreilly.threeutils.helpers.CoroutinesTestRule
import org.damienoreilly.threeutils.helpers.FakePreferenceStorage
import org.damienoreilly.threeutils.model.My3Error
import org.damienoreilly.threeutils.model.My3Token
import org.damienoreilly.threeutils.model.Profile
import org.damienoreilly.threeutils.model.TokenInfo
import org.damienoreilly.threeutils.repository.My3Repository
import org.damienoreilly.threeutils.repository.ThreeUtilsService
import org.damienoreilly.threeutils.worker.My3Worker
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class My3SetupViewModelTest : TestWatcher() {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesMainDispatcherRule = CoroutinesTestRule()

    @Test
    fun my3SetupViewModelTest_savesCredentialsAndSetupsJob() {

        val userName = "0830000000"
        val password = "testpass"

        val token = My3Token(TokenInfo("", ""),
                Profile("", emptyList(), "", ""))

        val my3Repository = mock<My3Repository> {
            onBlocking {
                login(anyString(), anyString())
            } doReturn ThreeUtilsService.Response.Success(token)
        }

        val workManager = mock<WorkManager> {
            on {
                enqueueUniquePeriodicWork(eq(My3Worker.MY3_USAGE_REFRESH_WORKER),
                        eq(ExistingPeriodicWorkPolicy.KEEP), any())
            } doReturn OperationImpl()
        }

        val preferenceStorage = FakePreferenceStorage()

        val viewModel = My3SetupViewModel(preferenceStorage, my3Repository, workManager)
        viewModel.username.value = userName
        viewModel.password.value = password

        viewModel.validateCredentials()

        assertThat(preferenceStorage.my3UserName).isEqualTo(userName)
        assertThat(preferenceStorage.my3Password).isEqualTo(password)
        assertThat(viewModel.loginError.value).isNull()

        verify(workManager, times(1))
                .enqueueUniquePeriodicWork(eq(My3Worker.MY3_USAGE_REFRESH_WORKER),
                        eq(ExistingPeriodicWorkPolicy.KEEP), any())
    }

    @Test
    fun my3SetupViewModelTest_showsLoginError() {

        val userName = "0830000000"
        val password = "testpass"

        val response = My3Error(errorCode = "Incorrect Login",
                errorDescription = "Bad Credentials",
                errorNumber = 403,
                defaultUserMessage = null)

        val my3Repository = mock<My3Repository> {
            onBlocking {
                login(anyString(), anyString())
            } doReturn ThreeUtilsService.Response.Error(response)
        }

        val workManager = mock<WorkManager>()

        val preferenceStorage = FakePreferenceStorage()

        val viewModel = My3SetupViewModel(preferenceStorage, my3Repository, workManager)
        viewModel.username.value = userName
        viewModel.password.value = password

        viewModel.validateCredentials()

        assertThat(preferenceStorage.my3UserName).isEqualTo(null)
        assertThat(preferenceStorage.my3Password).isEqualTo(null)
        assertThat(viewModel.loginError.value).isEqualTo("Bad Credentials")
    }
}