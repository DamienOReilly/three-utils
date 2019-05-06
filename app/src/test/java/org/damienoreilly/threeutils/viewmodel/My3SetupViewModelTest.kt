package org.damienoreilly.threeutils.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.damienoreilly.threeutils.CoroutinesMainDispatcherRule
import org.damienoreilly.threeutils.repository.My3Repository
import org.damienoreilly.threeutils.repository.PreferenceStorage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class My3SetupViewModelTest: TestWatcher() {

    @Mock
    lateinit var my3Repository: My3Repository

    @Mock
    lateinit var preferenceStorage: PreferenceStorage

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesMainDispatcherRule = CoroutinesMainDispatcherRule()

    private var viewModel: My3SetupViewModel? = null

    @Before
    fun setUp() {
        viewModel = My3SetupViewModel(preferenceStorage, my3Repository)
    }

    @Test
    fun my3SetupViewModelTest() {
        val viewModel = My3SetupViewModel(preferenceStorage, my3Repository)

        viewModel.username.value = "test"
        viewModel.password.value =  "test"

            viewModel.validateCredentials()
    }
}