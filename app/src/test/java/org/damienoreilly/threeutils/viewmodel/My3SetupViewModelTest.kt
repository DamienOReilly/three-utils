package org.damienoreilly.threeutils.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.damienoreilly.threeutils.repository.My3Repository
import org.damienoreilly.threeutils.repository.PreferenceStorage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class My3SetupViewModelTest {

    @Mock
    lateinit var my3Repository: My3Repository

    @Mock
    lateinit var preferenceStorage: PreferenceStorage

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Before
    fun setup() {

    }

    @Test
    fun my3SetupViewModelTest() {
        val viewModel = My3SetupViewModel(preferenceStorage, my3Repository)
        viewModel.password.postValue("test")
        viewModel.password.postValue( "test")

        viewModel.validateCredentials()
    }
}